package com.example.lupus_v2.model.manager

import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.roles.RoleFactory
import com.example.lupus_v2.model.roles.RoleResultEvent
import com.example.lupus_v2.model.roles.RoleType
import com.example.lupus_v2.model.voteStrategy.MostVotedPlayer

data class RoundResult(
    val events: List<RoleResultEvent>,
    val updatedPlayer: List<PlayerDetails>
)


class RoundResultManager(
    private val playerManager: PlayerManager,
    private val roleFactory: RoleFactory
) {
    private val _roundResultHistory = mutableListOf<RoundResult>()
    val roundResultHistory: List<RoundResult> = _roundResultHistory


    fun getRoundVoteResult(
        mostVotedPlayers: Map<RoleType, MostVotedPlayer>
    ): RoundResult {

        var roundResult = RoundResult(emptyList(), emptyList())
        mostVotedPlayers.forEach { (roleType, mostVotedPlayer) ->
            val role = roleFactory.createRole(roleType)
            val updatedRoundResult = role.roleRoundResult(mostVotedPlayers)

            roundResult = roundResult.copy(
                events = roundResult.events + updatedRoundResult.events,
                updatedPlayer = roundResult.updatedPlayer + updatedRoundResult.updatedPlayer
            )
        }

        //playerManager.updatePlayers(roundResult.updatedPlayer)
        _roundResultHistory.add(roundResult)    //citiziens are in a different round result than the other roles

        return roundResult
    }

    /**
     * @return null if there is no winner, otherwise the role type of the winner (Assassino or Cittadino)
     *
     */
    fun getWinners(): RoleType? {
        val playersAlive = playerManager.players.value.filter { it.alive }

        val assassinPlayer = playersAlive.filter { it.role.roleType == RoleType.Assassino }
        val otherPlayers = playersAlive.filter { it.role.roleType != RoleType.Assassino }

        if (assassinPlayer.isEmpty())
            return RoleType.Cittadino
        if (otherPlayers.size < assassinPlayer.size)
            return RoleType.Assassino
        return null
    }

    fun reset(){
        _roundResultHistory.clear()
    }

}