package com.example.lupus_v2.model.manager

import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.roles.RoleFactory
import com.example.lupus_v2.model.roles.RoleType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class GameState(
    val rolePlayerCount: Map<RoleType, Int> = emptyMap(),
    val rolesInRound: List<RoleType> = emptyList(),
    val rolesInGame: List<RoleType> = emptyList(),
    val round: Int = 0,
    val isDiscussion: Boolean = false,
    val playerManager: PlayerManager,
    val voteManager: VoteManager,
    val roundResultManager: RoundResultManager,
)

class GameManager(
    private val playerManager: PlayerManager,
    private val voteManager: VoteManager,
    private val roundResultManager: RoundResultManager,
    private val roleFactory: RoleFactory,
) {
    private val _gameState = MutableStateFlow(
        GameState(
            playerManager = playerManager,
            voteManager = voteManager,
            roundResultManager = roundResultManager
        )
    )
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

//    private val idealDistribution = mapOf(
//        RoleType.Assassino to 3,
//        RoleType.Medium to 1,
//        RoleType.FaciliCostumi to 2,
//        RoleType.Cupido to 1,
//        RoleType.Veggente to 1,
//        RoleType.Cittadino to 7
//    )

//    val essentialRoles = listOf(
//        RoleType.Assassino,
//        RoleType.Medium,
//        RoleType.FaciliCostumi,
//        RoleType.Veggente,
//        RoleType.Cittadino
//    )

    fun getFilteredDistribution(availableRoles: List<RoleType>): Map<RoleType, Int> {
        return GameRules.IDEAL_ROLE_DISTRIBUTION.filterKeys { availableRoles.contains(it) }
    }

    fun initRolePlayerCount(rolePlayerCount: Map<RoleType, Int>): Boolean {
        if (rolePlayerCount.values.sum() == gameState.value.playerManager.players.value.size) {
            _gameState.update { currentState ->
                currentState.copy(
                    rolePlayerCount = rolePlayerCount,
                    rolesInGame = rolePlayerCount.keys.toList()
                )
            }
            playerManager.assignRoleToPlayers(_gameState.value.rolePlayerCount.mapKeys {
                roleFactory.createRole(
                    it.key
                )
            })
            return true
        }
        return false
    }

    fun isLastPlayerOfRound(player: PlayerDetails): Boolean {
        return player.id == playerManager.playersAlive.last().id
    }

    fun canVote(player: PlayerDetails): Boolean {
        if (_gameState.value.isDiscussion) {
            return true
        }

        if (player.role.roleType == RoleType.Cittadino) {
            return false
        }

        val isRoundExclusion = player.role.canVote(_gameState.value.round)

        return isRoundExclusion
    }

    fun performRoundResult() {
        val mostVotedPlayers = _gameState.value.rolesInRound.filter { it != RoleType.Cittadino }.associateWith {
            voteManager.mostVotedPlayer(
                roleFactory.createRole(it)
            )
        }
        val roundResult = roundResultManager.getRoundVoteResult(mostVotedPlayers)
        playerManager.updatePlayers(roundResult.updatedPlayer)
    }

    fun performCitizenRoundResult() {
        val mostVotedPlayer = mapOf( RoleType.Cittadino to voteManager.mostVotedPlayer(roleFactory.createRole(RoleType.Cittadino)) )
        val roundResult = roundResultManager.getRoundVoteResult(mostVotedPlayer)
        playerManager.updatePlayers(roundResult.updatedPlayer)
    }

    fun getLastRoundResult() : RoundResult {
        return roundResultManager.roundResultHistory.last()
    }

    /**
     * @return true if there is a winner
     */
    fun gameIsOver(): Boolean {
        return roundResultManager.getWinners() != null
    }

    /**
     * @return the role of the winner
     * @throws IllegalStateException if there is no winner
     * @see gameIsOver
     */
    fun getWinnersRole() : RoleType {
        return roundResultManager.getWinners()!!
    }

    /**
     *update the role that vote in this round
     */
    fun startRound() {
        _gameState.update { currentState ->
            val newRound = currentState.round + 1
            currentState.copy(
                rolesInRound = currentState.rolesInGame.filter { roleFactory.createRole(it).canVote(newRound) && playerManager.playersAlive.any { player-> player.role.roleType == it } || it == RoleType.Cittadino},
                round = newRound
            )
        }
        voteManager.startRound(0, _gameState.value.rolesInRound)
    }

    /**
     * reset the game status. Necessary for correctly start a new game
     */
    fun reset(){
        _gameState.update { currentState ->
            currentState.copy(
                rolePlayerCount = emptyMap(),
                rolesInRound = emptyList(),
                rolesInGame = emptyList(),
                round = 0,
            )
        }
        playerManager.reset()
        voteManager.reset()
        roundResultManager.reset()
    }
}
