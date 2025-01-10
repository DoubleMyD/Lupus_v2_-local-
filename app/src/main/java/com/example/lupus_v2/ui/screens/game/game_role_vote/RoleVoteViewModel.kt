package com.example.lupus_v2.ui.screens.game.game_role_vote

import androidx.lifecycle.ViewModel
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.manager.VoteManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RoleVoteViewModel(
    private val voteManager: VoteManager
) : ViewModel(){
    private val _uiState = MutableStateFlow(RoleVoteUiState())
    val uiState: StateFlow<RoleVoteUiState> = _uiState.asStateFlow()

    fun resetVotedPlayers(){
        _uiState.update { currentState ->
            currentState.copy(votedPlayers = emptyList())
        }
        voteManager.resetVotedPlayers(uiState.value.voter.role.roleType)
    }

    fun initVote(voter: PlayerDetails, playerToVote: List<PlayerDetails>) {
        _uiState.update { currentState ->
            currentState.copy(
                voter = voter,
                playersToVote = playerToVote
            )
        }
    }

    fun togglePlayerVoted(player: PlayerDetails) {
        _uiState.update { currentState ->
            val updatedPlayersSelected = if (currentState.votedPlayers.contains(player)) {
                currentState.votedPlayers - player
            } else {
                if(currentState.votedPlayers.size >= _uiState.value.voter.role.getVoteStrategyCount())
                    return
                currentState.votedPlayers + player
            }
            currentState.copy(votedPlayers = updatedPlayersSelected)
        }
    }

    fun onConfirmVoteClick(votedPlayers: List<PlayerDetails>) : Result<Boolean>{
        var playerFinishedVoting = false
        var errorOccurred = false

        for(player in votedPlayers){
            val result = voteManager.vote(uiState.value.voter, player)
            if (result.isSuccess) {
                playerFinishedVoting = true
            } else {
                addPlayerToBounce(player) // Update UI state for an error
                errorOccurred = true
                break // Stop processing on the first error, if required
            }
        }

        return if (errorOccurred) {
            Result.failure(Exception("An error occurred during voting."))
        } else {
            Result.success(playerFinishedVoting)
        }
    }

    private fun addPlayerToBounce(player: PlayerDetails){
        _uiState.update { currentState ->
            currentState.copy(playersNotValidBouncing = currentState.playersNotValidBouncing + player)
        }
    }

    fun removePlayerToBounce(player: PlayerDetails){
        _uiState.update { currentState ->
            currentState.copy(playersNotValidBouncing = currentState.playersNotValidBouncing - player)
        }
    }


}