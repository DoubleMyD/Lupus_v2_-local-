package com.example.lupus_v2.data.database.typeConverter

import androidx.room.TypeConverter

// TypeConverter for converting List<Int> to String and vice versa
class IntegerList_String_Converter {

    @TypeConverter
    fun fromStringToIntegerList(integerString: String?): List<Int> {
        if (integerString.isNullOrEmpty()) {
            return emptyList()
        }
        return integerString.split(",").mapNotNull { it.toIntOrNull() } // Safely map to Int
    }

    @TypeConverter
    fun fromIntegerListToString(integerList: List<Int>?): String {
        return integerList?.joinToString(",") ?: "" // Handle null or empty list
    }
}