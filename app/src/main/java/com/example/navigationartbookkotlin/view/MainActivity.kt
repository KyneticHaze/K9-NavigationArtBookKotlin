package com.example.navigationartbookkotlin.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation
import com.example.navigationartbookkotlin.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_item, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.addArt -> {
                val action = RecyclerFragmentDirections.actionRecyclerFragmentToArtFragment("new", 0)
                Navigation.findNavController(this, R.id.fragmentContainerView).navigate(action)
            }
            R.id.return_main -> {
                val action = ArtFragmentDirections.actionArtFragmentToRecyclerFragment()
                Navigation.findNavController(this, R.id.fragmentContainerView).navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}