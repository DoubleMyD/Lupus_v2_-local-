package com.example.lupus_v2.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.lupus_v2.data.database.dao.PlayerDao
import com.example.lupus_v2.data.database.dao.PlayersListDao
import com.example.lupus_v2.data.database.entity.Player
import com.example.lupus_v2.data.database.typeConverter.IntegerList_String_Converter
import com.example.lupus_v2.data.database.entity.PlayersList

@Database(entities = [Player::class, PlayersList::class], version = 1, exportSchema = false)
@TypeConverters(IntegerList_String_Converter::class) // Register the converter
abstract class LupusDatabase() : RoomDatabase() {

    abstract fun playerDao(): PlayerDao
    abstract fun playersListDao(): PlayersListDao

    companion object {
        @Volatile
        private var INSTANCE: LupusDatabase? = null

        fun getDatabase(context: Context): LupusDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context = context, klass = LupusDatabase::class.java, name = "lupus_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}