package com.example.lupus_v2.data.repository

import com.example.lupus_v2.data.database.dao.PlayerDao
import com.example.lupus_v2.data.database.entity.Player
import kotlinx.coroutines.flow.Flow

class OfflinePlayersRepository(private val playerDao: PlayerDao) : PlayersRepository {

    override fun getAllPlayersStream(): Flow<List<Player>> = playerDao.getAllPlayers()

    override fun getPlayerStream(id: Int): Flow<Player?> = playerDao.getPlayer(id)

    override suspend fun insertPlayer(player: Player) = playerDao.insert(player)

    override suspend fun deletePlayer(player: Player) = playerDao.delete(player)

    override suspend fun updatePlayer(player: Player) = playerDao.update(player)

    override fun getPlayerByName(name: String): Flow<Player?> = playerDao.getPlayerByName(name)

    override suspend fun getPlayersByIds(playerIds: List<Int>): List<Player> =
        playerDao.getPlayersByIds(playerIds)

}