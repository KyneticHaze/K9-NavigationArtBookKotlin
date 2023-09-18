package com.example.navigationartbookkotlin.view.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Ignore
import androidx.room.Insert
import androidx.room.Query
import com.example.navigationartbookkotlin.view.model.Art
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface ArtDao {
    @Query("SELECT artName,id FROM Art")
    fun getNameAndId() : Flowable<List<Art>>
    @Query("SELECT * FROM Art WHERE id = :uid")
    fun getId(uid : Int) : Flowable<Art>
    @Insert
    fun insert(art: Art) : Completable
    @Delete
    fun delete(art: Art) : Completable
}