package com.example.lupus_v2.model.manager

import android.util.Log
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.roles.Role
import com.example.lupus_v2.model.roles.RoleFactory
import com.example.lupus_v2.model.roles.RoleType
import com.example.lupus_v2.model.voteStrategy.MostVotedPlayer

private const val TAG = "VoteManager"

//summary of the round
data class RoundVotes(
    val round: Int,
    val votes: Map<RoleType, RoleVotes>,
)

//summary of the votes related to a role, per round
data class RoleVotes(
    val role: Role,
    val voters: List<PlayerDetails>,
    val votedPlayerDetails: List<PlayerDetails>,
    val votesPairPlayers: List<VotePairPlayers>
)

//helper class to store the pair of players using significant name
data class VotePairPlayers(
    val voter: PlayerDetails,
    val votedPlayer: PlayerDetails
)

class VoteManager(
    private val roleFactory: RoleFactory
){
    private var roundVotingHistory: MutableList<RoundVotes> = mutableListOf()

//    /**
//     * Starts a new game with the given roles.
//     */
//    fun startGame(rolesInGame: List<RoleType>){
//        roundVotingHistory.clear()
//        startRound(0, rolesInGame)
//    }

    fun resetVotedPlayers(roleType: RoleType){
        // Get the last round's votes to modify
        val currentRoundVote = roundVotingHistory.lastOrNull() ?: return

        roundVotingHistory[roundVotingHistory.lastIndex] = currentRoundVote.copy(
            votes = currentRoundVote.votes.toMutableMap().apply {
                this[roleType] = this[roleType]!!.copy(
                    votedPlayerDetails = emptyList(),
                    votesPairPlayers = emptyList()
                )
            }
        )

    }
    /**
     * Starts a new round with the given set of role to vote
     */
    fun startRound(round: Int, rolesInGame: List<RoleType>){
        val initialVotes = rolesInGame.associateWith { RoleVotes(roleFactory.createRole(it), emptyList(), emptyList(), emptyList()) }
        roundVotingHistory.add(RoundVotes(round, initialVotes))
    }

    /**
     * perform the vote and update the voting history
     * Returns a Success with true if the voter has finished voting, false otherwise.
     * Returns a Failure with an exception if the vote is not valid
     */
    fun vote(voter: PlayerDetails, votedPlayer: PlayerDetails): Result<Boolean>{

        // Get the last round's votes to modify
        val currentRoundVote = roundVotingHistory.lastOrNull()
            ?: return Result.failure(IllegalStateException("No active round found."))
        // Fetch RoleVotes for the voter's role
        Log.d("VoteManager", "currentRoundVote: ${currentRoundVote.votes.map { it.key }}")
        val roleVotes = currentRoundVote.votes[voter.role.roleType]
            ?: return Result.failure(IllegalArgumentException("Role ${voter.role} not found."))


        return try {
            // Perform the vote using the role's strategy
            val voteResult = voter.role.vote(roleVotes, voter, votedPlayer)

            // Replace the last RoundVotes with updated votes
            val updatedVotes = currentRoundVote.votes.toMutableMap()
            updatedVotes[voter.role.roleType] = voteResult.updatedRoleVotes

            // Update roundVotingHistory immutably
            roundVotingHistory[roundVotingHistory.lastIndex] = currentRoundVote.copy(votes = updatedVotes)

            Result.success(voteResult.playerFinishedVoting)
        } catch(e: IllegalStateException){
            Log.d(TAG, "vote ${voter.role.roleType}: ${e.message}")
            Result.failure(e)
        }
    }

    fun mostVotedPlayer(role: Role): MostVotedPlayer {
        val currentRoundVote = roundVotingHistory.lastOrNull()
            ?: return MostVotedPlayer.NoVotes

        val roleVotes = currentRoundVote.votes[role.roleType]
            ?: return MostVotedPlayer.NoVotes

        return role.mostVotedPlayer(roleVotes)
    }

    fun reset(){
        roundVotingHistory.clear()
    }

}