package com.example.navigationartbookkotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import com.example.navigationartbookkotlin.databinding.FragmentArtBinding
import com.google.android.material.snackbar.Snackbar

class ArtFragment : Fragment() {
    private var _binding: FragmentArtBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionResultLauncher : ActivityResultLauncher<String>
    private var imageUri : Uri? = null
    private var permissionMedia = Manifest.permission.READ_MEDIA_IMAGES
    private var mediaPick = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentArtBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    fun imagePicker(view: View) {
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
                    binding.imagePicker.setImageURI(imageUri)
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
}