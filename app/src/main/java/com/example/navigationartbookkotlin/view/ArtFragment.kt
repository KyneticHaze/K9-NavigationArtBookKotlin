package com.example.navigationartbookkotlin.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.room.Room
import com.example.navigationartbookkotlin.R
import com.example.navigationartbookkotlin.databinding.FragmentArtBinding
import com.example.navigationartbookkotlin.view.model.Art
import com.example.navigationartbookkotlin.view.roomdb.ArtDao
import com.example.navigationartbookkotlin.view.roomdb.ArtDatabase
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream

class ArtFragment : Fragment() {
    private var _binding: FragmentArtBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionResultLauncher : ActivityResultLauncher<String>
    private var imageUri : Uri? = null
    private var imageBitmap : Bitmap? = null
    private var permissionMedia = Manifest.permission.READ_MEDIA_IMAGES
    private var mediaPick = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    private lateinit var db : ArtDatabase
    private lateinit var artDao : ArtDao
    private val cDisposable = CompositeDisposable()
    private var artFromMain : Art? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()

        db = Room.databaseBuilder(requireContext(),ArtDatabase::class.java, "Arts").build()
        artDao = db.artDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentArtBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.imagePicker.setOnClickListener { imagePicker(view) }
        binding.saveArtButton.setOnClickListener { saveArtButton(view) }
        binding.deleteArtButton.setOnClickListener { deleteArtButton(view) }

        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val info = ArtFragmentArgs.fromBundle(it).info

            when (info) {
                "new" -> {
                    binding.artNameText.setText("")
                    binding.artistNameText.setText("")
                    binding.artYearNameText.setText("")

                    binding.deleteArtButton.visibility = View.GONE
                    binding.saveArtButton.visibility = View.VISIBLE

                    val bitmap = BitmapFactory.decodeResource(context?.resources, R.drawable.image_picker)
                    binding.imagePicker.setImageBitmap(bitmap)
                }
                "old" -> {
                    binding.saveArtButton.visibility = View.GONE
                    binding.deleteArtButton.visibility = View.VISIBLE

                    val selectedId = ArtFragmentArgs.fromBundle(it).id
                    cDisposable.add(artDao.getId(selectedId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponseArgs)
                    )
                }

                else -> {}
            }
        }
    }

    private fun handleResponseArgs(art : Art) {
        artFromMain = art
        binding.artNameText.setText(art.artName)
        binding.artistNameText.setText(art.artistName)
        binding.artYearNameText.setText(art.artYearName)

        art.image?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            binding.imagePicker.setImageBitmap(bitmap)
        }
    }

    private fun saveArtButton(view: View) {
        val artName = binding.artNameText.text.toString()
        val artistName = binding.artistNameText.text.toString()
        val artYearName = binding.artYearNameText.text.toString()

        if (imageBitmap != null) {
            val smallBitmap = makeSmallerBitmap(imageBitmap!!, 400)

            val byteArrayOutputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            val art = Art(artName, artistName, artYearName, byteArray)

            cDisposable.add(artDao.insert(art)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
            )
        }
    }

    private fun deleteArtButton(view: View) {
        artFromMain?.let {
            cDisposable.add(artDao.delete(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
            )
        }
    }
    private fun handleResponse() {
        val action = ArtFragmentDirections.actionArtFragmentToRecyclerFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }

    private fun makeSmallerBitmap(image: Bitmap, scale: Int): Bitmap {
        var width = image.width
        var height = image.height

        val imageRatio : Double = width.toDouble() / height.toDouble()

        if (imageRatio > 1) {
            // landscape
            width = scale
            val scaledHeight = width / imageRatio
            height = scaledHeight.toInt()
        } else {
            // portrait
            height = scale
            val scaledWidth = height * imageRatio
            width = scaledWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, false)
    }

    private fun imagePicker(view: View) {
        activity?.let {
            if (ContextCompat.checkSelfPermission(requireContext(), permissionMedia) != PackageManager.PERMISSION_GRANTED) {
                // Öncelikle uygulamanın başlangıçta iznini sorgulayalım
                if (ActivityCompat.shouldShowRequestPermissionRationale(it, permissionMedia)) {
                    // izin isteyelim
                    Snackbar.make(view, "Give Permission for gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission") {
                            permissionResultLauncher.launch(permissionMedia)
                        }.show()
                } else {
                    // permissionLauncher gösterelim reddedilirse Toast bastırsın evetlenirse direkt activityResultLauncher çalışsın
                    permissionResultLauncher.launch(permissionMedia)
                }
            } else {
                // Zaten izni varsa
                val intentToGallery = Intent(mediaPick)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            if(result.resultCode == AppCompatActivity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    imageUri = intent.data

                    try {
                        val source = ImageDecoder.createSource(
                            requireActivity().contentResolver,
                            imageUri!!
                        )
                        imageBitmap = ImageDecoder.decodeBitmap(source)
                        binding.imagePicker.setImageBitmap(imageBitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        permissionResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {permissionBoolean ->
            if (permissionBoolean) {
                // izin olumlu ise
                val intentToGallery = Intent(mediaPick)
                activityResultLauncher.launch(intentToGallery)
            } else {
                // izin reddedilirse
                Toast.makeText(requireContext(),"Permission Needed!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}