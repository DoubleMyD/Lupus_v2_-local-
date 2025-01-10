package com.example.lupus_v2.data.repository

import androidx.room.Transaction
import com.example.lupus_v2.R
import com.example.lupus_v2.data.database.dao.PlayersListDao
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.PlayerImageSource
import com.example.lupus_v2.model.toPlayerDetails
import com.example.lupus_v2.data.database.entity.PlayersList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class OfflinePlayersListsRepository(
    private val playersListDao: PlayersListDao,
    private val playersRepository: PlayersRepository
) : PlayersListsRepository {

    override fun getAllPlayersListsStream(): Flow<List<PlayersList>> =
        playersListDao.getAllPlayersLists()

    override fun getPlayersListStream(id: Int): Flow<PlayersList?> =
        playersListDao.getPlayersList(id)

    override suspend fun insertPlayersList(playersList: PlayersList) =
        playersListDao.insert(playersList)

    override suspend fun deletePlayersList(playersList: PlayersList) =
        playersListDao.delete(playersList)

    override suspend fun updatePlayersList(playersList: PlayersList) =
        playersListDao.update(playersList)


    @Transaction
    override suspend fun getAllListsWithPlayersDetails(): Map<PlayersList, List<PlayerDetails>> {
        val playersLists = playersListDao.getAllPlayersLists().first()
        return playersLists.associateWith { playersList ->
            getPlayersDetailsFromPlayersList(playersList.id)
        }
    }

    @Transaction
    override suspend fun getPlayersDetailsFromPlayersList(listId: Int): List<PlayerDetails> {
        val playersList = playersListDao.getPlayersList(listId).first()
        val players = playersRepository.getPlayersByIds(playersList.playersId)
        return players.map { player -> player.toPlayerDetails() }
    }

    @Transaction // Ensure atomic operation
    override suspend fun updatePlayersIdOfList(
        listId: Int,
        addedPlayers: List<Int>,
        removedPlayers: List<Int>
    ) {
        val playersList = playersListDao.getPlayersList(listId).first()
        val updatedPlayerIds =
            (playersList.playersId + addedPlayers - removedPlayers.toSet()).distinct()

        playersListDao.update(playersList.copy(playersId = updatedPlayerIds))
    }


    override suspend fun addPlayerIdToList(listId: Int, vararg playerIds: Int) {
        updatePlayerIdsInList(listId) { existingIds ->
            (existingIds + playerIds).distinct().map { it as Int }
        }
    }

    override suspend fun removePlayerIdFromList(listId: Int, vararg playerIds: Int) {
        updatePlayerIdsInList(listId) { existingIds ->
            existingIds - playerIds.toSet()
        }
    }

    /**
     * Helper function to update player IDs in a transactional manner, avoiding duplicates or inconsistencies.
     */
    private suspend fun updatePlayerIdsInList(listId: Int, updateLogic: (List<Int>) -> List<Int>) {
        val playersList = playersListDao.getPlayersList(listId).first()
        val updatedPlayersId = updateLogic(playersList.playersId)

        playersListDao.update(playersList.copy(playersId = updatedPlayersId))
    }
}
