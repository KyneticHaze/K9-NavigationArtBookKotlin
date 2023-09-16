package com.example.navigationartbookkotlin.view.model

import androidx.room.Entity

@Entity(tableName = "artTable")
class Art(
    val artName: String,
    val artistName: String,
    val artYearName: String,
    val image: ByteArray
)