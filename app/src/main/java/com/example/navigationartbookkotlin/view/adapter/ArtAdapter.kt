package com.example.navigationartbookkotlin.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationartbookkotlin.databinding.RecyclerItemBinding
import com.example.navigationartbookkotlin.view.RecyclerFragmentDirections
import com.example.navigationartbookkotlin.view.model.Art

class ArtAdapter(private val artList : List<Art>) : RecyclerView.Adapter<ArtAdapter.ArtViewHolder>() {

    inner class ArtViewHolder(val binding : RecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtViewHolder {
        val binding = RecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return artList.size
    }

    override fun onBindViewHolder(holder: ArtViewHolder, position: Int) {
        holder.binding.recyclerItem.text = artList[position].artName
        holder.itemView.setOnClickListener {
            val action = RecyclerFragmentDirections.actionRecyclerFragmentToArtFragment("old", artList[position].id)
            Navigation.findNavController(it).navigate(action)
        }
    }
}