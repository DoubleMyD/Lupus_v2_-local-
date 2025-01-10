package com.example.lupus_v2.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
class Player (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val imageSource: String
)
