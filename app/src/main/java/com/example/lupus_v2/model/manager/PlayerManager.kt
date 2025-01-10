package com.example.lupus_v2.model.manager

import android.util.Log
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.roles.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class PlayerManager() {
    private val _players: MutableStateFlow<List<PlayerDetails>> =
        MutableStateFlow(emptyList())
    val players: StateFlow<List<PlayerDetails>> = _players.asStateFlow()


    private var currentPlayerIndex: Int = 0
        set(value) {
            field = value.coerceIn(0, playersAlive.lastIndex.coerceAtLeast(0))
        }

    val currentPlayer: PlayerDetails
        get() = playersAlive[currentPlayerIndex]

    val previousPlayer: PlayerDetails
        get() = players.value[(currentPlayerIndex-- + players.value.size) % players.value.size]

    val playersAlive: List<PlayerDetails>
        get() = players.value.filter { it.alive }

    fun goToNextPlayer() {
        if (playersAlive.isEmpty()) return // Ensure there are still alive players
        currentPlayerIndex = (currentPlayerIndex + 1) % playersAlive.size
    }

    /**
     * Initialize the player list. Intended for one-time for game setup.
     */
    fun initializePlayers(playerDetails: List<PlayerDetails>) {
        _players.value = playerDetails
    }

    fun updateOrder(newOrder: List<Int>) {
        _players.update { players ->
            newOrder.mapNotNull { id -> players.find { it.id == id } }
        }
    }


    fun updatePlayers(updatedPlayers: List<PlayerDetails>) {
        _players.update { players ->
            players.map { player ->
                updatedPlayers.find { it.id == player.id } ?: player
            }
        }
        if (currentPlayerIndex >= playersAlive.size) {
            currentPlayerIndex = 0 // Reset to a valid position
        }
    }

    fun addPlayers(vararg playerDetails: PlayerDetails) {
        _players.update { currentPlayers -> currentPlayers + playerDetails }
    }

    fun removePlayer(playerDetails: PlayerDetails) {
        _players.update { currentPlayers -> currentPlayers - playerDetails }
    }

    // Update a specific player’s role by player ID
    fun updatePlayerRole(playerId: Int, newRole: Role) {
        _players.update { currentPlayers ->
            currentPlayers.map { player ->
                if (player.id == playerId) player.copy(role = newRole) else player
            }
        }
    }

    /**
     * Assign roles to the specified number of players based on the provided map.
     * Ensures no more roles are assigned than there are players available.
     */
    fun assignRoleToPlayers(playersForRole: Map<Role, Int>) {
        // Shuffle the list of player IDs to randomize the order
        val shuffledPlayers = players.value.shuffled()

        // Ensure there are enough players for the roles specified
        val totalRolesRequired = playersForRole.values.sum()
        if (totalRolesRequired > shuffledPlayers.size) {
            throw IllegalArgumentException("Not enough players to assign the requested roles.")
        }

        // Track the current index in the shuffled list
        var currentIndex = 0

        // Iterate through the roles and assign the correct count of players for each role
        playersForRole.forEach { (role, count) ->
            repeat(count) {
                // Assign role to the player at the current index in the shuffled list
                val playerId = shuffledPlayers[currentIndex].id
                updatePlayerRole(playerId, role)

                // Move to the next player
                currentIndex++
            }
        }
    }

    fun resetIndex(){
        currentPlayerIndex = 0
    }

    fun reset(){
        _players.value = emptyList()
        resetIndex()
    }


}