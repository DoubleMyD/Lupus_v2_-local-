package com.example.lupus_v2.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lupus_v2.data.database.entity.PlayersList
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayersListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(playersList: PlayersList)

    @Update
    suspend fun update(playersList: PlayersList)

    @Delete
    suspend fun delete(playersList: PlayersList)

    @Query("SELECT id from players_lists")
    fun getAllPlayersListsId(): Flow<List<Int>>

    @Query("SELECT * from players_lists ORDER BY name ASC")
    fun getAllPlayersLists(): Flow<List<PlayersList>>

    @Query("SELECT * from players_lists WHERE id = :id")
    fun getPlayersList(id: Int): Flow<PlayersList>

}