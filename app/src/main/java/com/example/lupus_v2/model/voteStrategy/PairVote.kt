package com.example.lupus_v2.model.voteStrategy

import android.util.Log
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.manager.RoleVotes

class PairVote : VoteStrategy(
    voteStrategyCount = 2
) {

    override fun vote(lastVotingState: RoleVotes, voter: PlayerDetails, votedPlayer: PlayerDetails): VoteResult {
        // Reuse the OneVote logic for voting and then check vote count
        val voteResult = OneVote().vote(lastVotingState, voter, votedPlayer)

        // Check if the player has voted twice
        val hasFinishedVoting = voteResult.updatedRoleVotes.votesPairPlayers.count { it.voter == voter } == 2

        return VoteResult(voteResult.updatedRoleVotes, hasFinishedVoting)
    }

    override fun mostVotedPlayer(lastVotingState: RoleVotes): MostVotedPlayer {
        val votesPairPlayers = lastVotingState.votesPairPlayers
        // Map to store the count of each pair (ignoring order)
        val pairCountMap = mutableMapOf<Pair<PlayerDetails, PlayerDetails>, Int>()

        // Iterate through the votes and count pairs
        for (i in 0 until votesPairPlayers.size / 2) {
            val firstVote = votesPairPlayers[i * 2].votedPlayer
            val secondVote = votesPairPlayers[i * 2 + 1].votedPlayer
            val normalizedPair = if (firstVote.name > secondVote.name) {
                Pair(secondVote, firstVote)
            } else {
                Pair(firstVote, secondVote)
            }

            pairCountMap[normalizedPair] = pairCountMap.getOrDefault(normalizedPair, 0) + 1
        }

        if (pairCountMap.isEmpty()) return MostVotedPlayer.NoVotes

        // Check if there's a tie by finding how many pairs have the same max count
        val maxCount = pairCountMap.values.maxOrNull() ?: return MostVotedPlayer.NoVotes
        val pairsWithMaxCount = pairCountMap.filterValues { it == maxCount }

        return if (pairsWithMaxCount.size > 1) {
            MostVotedPlayer.Tie(pairsWithMaxCount.keys.map { listOf(it.first, it.second) }.flatten().distinct())
        } else {
            val maxPair = pairsWithMaxCount.keys.first()
            MostVotedPlayer.PairPlayers(maxPair.first, maxPair.second)
        }
    }

    override fun handleTie(mostVotedPlayers: List<PlayerDetails>): MostVotedPlayer {
        val pairs = mostVotedPlayers.zipWithNext()
        val randomPairs = pairs.random()

        return MostVotedPlayer.PairPlayers(randomPairs.first, randomPairs.second)
    }
}