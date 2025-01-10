package com.example.lupus_v2.ui.screens.game.game_prepare.choose_players

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.manager.PlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChoosePlayersViewModel(
    private val playerManager: PlayerManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChoosePlayersUiState())
    val uiState: StateFlow<ChoosePlayersUiState> = _uiState.asStateFlow()


    fun togglePlayerSelected(player: PlayerDetails) {
        _uiState.update { currentState ->
            val updatedPlayersSelected = if (currentState.playersSelected.contains(player)) {
                currentState.playersSelected - player
            } else {
                currentState.playersSelected + player
            }
            currentState.copy(playersSelected = updatedPlayersSelected)
        }
    }

    fun confirmPlayers() : Boolean{
        if(_uiState.value.playersSelected.size < 6){
            Log.d("ChoosePlayersViewModel", "Not enough players selected")
            return false
        }

        playerManager.initializePlayers(_uiState.value.playersSelected)
        return true
    }
}