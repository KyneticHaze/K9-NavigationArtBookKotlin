package com.example.navigationartbookkotlin.view.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.navigationartbookkotlin.view.model.Art

@Dao
interface ArtDao {
    @Query("SELECT * FROM artTable")
    fun getAll() : List<Art>
    @Insert
    fun insert(art: Art)
    @Delete
    fun delete(art: Art)
}