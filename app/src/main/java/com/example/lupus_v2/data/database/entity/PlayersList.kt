package com.example.lupus_v2.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.lupus_v2.data.database.typeConverter.IntegerList_String_Converter

@Entity(tableName = "players_lists")
data class PlayersList(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    @TypeConverters(IntegerList_String_Converter::class) // Apply TypeConverter
    val playersId: List<Int> = emptyList()
)


