package com.example.lupus_v2.data.repository

import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.data.database.entity.PlayersList
import kotlinx.coroutines.flow.Flow

interface PlayersListsRepository {

    fun getAllPlayersListsStream(): Flow<List<PlayersList>>

    fun getPlayersListStream(id: Int): Flow<PlayersList?>

    suspend fun insertPlayersList(playersList: PlayersList)

    suspend fun deletePlayersList(playersList: PlayersList)

    suspend fun updatePlayersList(playersList: PlayersList)


    suspend fun getAllListsWithPlayersDetails(): Map<PlayersList, List<PlayerDetails>>
    suspend fun getPlayersDetailsFromPlayersList(listId: Int): List<PlayerDetails>

    suspend fun updatePlayersIdOfList(listId: Int, addedPlayers: List<Int>, removedPlayers: List<Int>)
    suspend fun addPlayerIdToList(listId: Int, vararg playerIds: Int)
    suspend fun removePlayerIdFromList(listId: Int, vararg playerIds: Int)
}