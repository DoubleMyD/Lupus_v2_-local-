package com.example.lupus_v2.model.voteStrategy

import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.manager.PlayerManager
import com.example.lupus_v2.model.manager.RoleVotes

class CopyRole(
    private val playerManager: PlayerManager
) : VoteStrategy(
    voteStrategyCount = 1
) {

    override fun vote(lastVotingState: RoleVotes, voter: PlayerDetails, votedPlayer: PlayerDetails): VoteResult {
        // Update the voter's role to match the voted player's role
        playerManager.updatePlayerRole(voter.id, votedPlayer.role)

        // Return the vote result without modifying RoleVotes directly
        return VoteResult(lastVotingState, playerFinishedVoting = true)
    }

    override fun mostVotedPlayer(lastVotingState: RoleVotes): MostVotedPlayer {
        return MostVotedPlayer.NoVotes
    }

    override fun handleTie(mostVotedPlayers: List<PlayerDetails>): MostVotedPlayer {
        return MostVotedPlayer.NoVotes
    }
}