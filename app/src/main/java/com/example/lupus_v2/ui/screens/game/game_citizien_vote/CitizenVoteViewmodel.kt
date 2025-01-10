package com.example.lupus_v2.ui.screens.game.game_citizien_vote

import androidx.lifecycle.ViewModel
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.manager.VoteManager
import com.example.lupus_v2.model.roles.RoleFactory
import com.example.lupus_v2.model.roles.RoleType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CitizenVoteViewmodel(
    private val voteManager: VoteManager,
    private val roleFactory: RoleFactory
) : ViewModel() {
    private val _uiState = MutableStateFlow(CitizenVoteUiState())
    val uiState: StateFlow<CitizenVoteUiState> = _uiState.asStateFlow()

    fun updateVoterPlayer(newVoter: PlayerDetails) {
        _uiState.value = _uiState.value.copy(
            voter = newVoter
        )
    }

    fun votePlayer(voter: PlayerDetails, votedPlayer: PlayerDetails): Result<Boolean> {
        val voteValid = voteManager.vote(
            voter.copy(role = roleFactory.createRole(RoleType.Cittadino)),
            votedPlayer
        ).getOrThrow()
        if (voteValid) {
            _uiState.value = _uiState.value.copy(
                playersThatAlreadyVoted = _uiState.value.playersThatAlreadyVoted + votedPlayer,
                voteCount = _uiState.value.voteCount + (votedPlayer.id to (_uiState.value.voteCount[votedPlayer.id]
                    ?: 0) + 1)
            )
        }
        return Result.success(voteValid)
    }
}