package com.example.navigationartbookkotlin.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.room.Room
import com.example.navigationartbookkotlin.databinding.FragmentRecyclerBinding
import com.example.navigationartbookkotlin.view.adapter.ArtAdapter
import com.example.navigationartbookkotlin.view.model.Art
import com.example.navigationartbookkotlin.view.roomdb.ArtDao
import com.example.navigationartbookkotlin.view.roomdb.ArtDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class RecyclerFragment : Fragment() {
    private var _binding: FragmentRecyclerBinding? = null
    private val binding get() = _binding!!
    private lateinit var db : ArtDatabase
    private lateinit var artDao : ArtDao
    private val cDisposable = CompositeDisposable()
    private lateinit var artAdapter : ArtAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(requireContext(), ArtDatabase::class.java, "Arts").build()
        artDao = db.artDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecyclerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()
    }

    private fun getData() {
        cDisposable.add(artDao.getNameAndId()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse)
        )
    }

    private fun handleResponse(artList : List<Art>) {
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        artAdapter = ArtAdapter(artList)
        binding.recycler.adapter = artAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}