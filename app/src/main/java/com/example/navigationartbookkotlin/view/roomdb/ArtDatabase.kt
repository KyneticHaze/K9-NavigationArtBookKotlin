package com.example.navigationartbookkotlin.view.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.navigationartbookkotlin.view.model.Art

@Database(entities = [Art::class], version = 2)
abstract class ArtDatabase : RoomDatabase() {
    abstract fun artDao() : ArtDao
}