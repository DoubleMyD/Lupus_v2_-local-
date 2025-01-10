package com.example.lupus_v2.model.voteStrategy

import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.manager.RoleVotes
import com.example.lupus_v2.model.manager.VotePairPlayers

class OneVote : VoteStrategy(
    voteStrategyCount = 1
) {

    override fun vote(
        lastVotingState: RoleVotes,
        voter: PlayerDetails,
        votedPlayer: PlayerDetails
    ): VoteResult {
        val updatedVotes = lastVotingState.votedPlayerDetails + votedPlayer
        val updatedVotePairs =
            lastVotingState.votesPairPlayers + VotePairPlayers(voter, votedPlayer)

        val updatedRoleVotes = lastVotingState.copy(
            votedPlayerDetails = updatedVotes,
            votesPairPlayers = updatedVotePairs
        )
        return VoteResult(updatedRoleVotes, true)
    }

    override fun mostVotedPlayer(lastVotingState: RoleVotes): MostVotedPlayer {
        val voteCounts = lastVotingState.votedPlayerDetails.groupingBy { it }.eachCount()

        if (voteCounts.isEmpty()) return MostVotedPlayer.NoVotes

        val maxVoteCount = voteCounts.values.maxOrNull() ?: return MostVotedPlayer.NoVotes
        val mostVotedPlayers = voteCounts.filterValues { it == maxVoteCount }.keys

        return if (mostVotedPlayers.size > 1) {
            MostVotedPlayer.Tie(mostVotedPlayers.toList())
        } else {
            MostVotedPlayer.SinglePlayer(mostVotedPlayers.first())
        }
    }

    override fun handleTie(mostVotedPlayers: List<PlayerDetails>): MostVotedPlayer {
        return super.handleTieSinglePlayerRandom(mostVotedPlayers)
    }

}