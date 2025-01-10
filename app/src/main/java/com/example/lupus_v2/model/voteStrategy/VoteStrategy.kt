package com.example.lupus_v2.model.voteStrategy

import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.manager.RoleVotes

//helper class to store the result of a single vote and to signal if the player has finished voting
data class VoteResult(
    val updatedRoleVotes: RoleVotes,
    val playerFinishedVoting: Boolean
)

sealed class MostVotedPlayer {
    data class SinglePlayer(val playerDetails: PlayerDetails) : MostVotedPlayer()
    data class PairPlayers(val playerDetails1: PlayerDetails, val playerDetails2: PlayerDetails) : MostVotedPlayer()
    data class Tie(val players: List<PlayerDetails>) : MostVotedPlayer()
    data object NoVotes : MostVotedPlayer() // Represents no votes were cast
}

abstract class VoteStrategy(
    val voteStrategyCount: Int = 1
) {
    abstract fun vote(lastVotingState: RoleVotes, voter: PlayerDetails, votedPlayer: PlayerDetails): VoteResult
    abstract fun mostVotedPlayer(lastVotingState: RoleVotes): MostVotedPlayer

    abstract fun handleTie(mostVotedPlayers: List<PlayerDetails>): MostVotedPlayer

    protected fun handleTieSinglePlayerRandom(mostVotedPlayers: List<PlayerDetails>): MostVotedPlayer {
        return MostVotedPlayer.SinglePlayer(mostVotedPlayers.random())
    }
}