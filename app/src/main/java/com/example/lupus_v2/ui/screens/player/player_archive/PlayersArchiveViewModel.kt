package com.example.lupus_v2.ui.screens.player.player_archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupus_v2.data.database.entity.Player
import com.example.lupus_v2.data.repository.PlayersRepository
import com.example.lupus_v2.model.toPlayerDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlayersArchiveViewModel(
    private val playersRepository: PlayersRepository
) : ViewModel() {

    val databasePlayers: StateFlow<PlayersArchiveUiState> =
        playersRepository.getAllPlayersStream().map { players ->
            PlayersArchiveUiState(
                players = players.map { it.toPlayerDetails() }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = PlayersArchiveUiState()
        )

    fun deletePlayer(player: Player) {
        viewModelScope.launch {
            playersRepository.deletePlayer(player)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 10_000L
    }
}