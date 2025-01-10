package com.example.lupus_v2.model.roles

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.manager.RoleVotes
import com.example.lupus_v2.model.manager.RoundResult
import com.example.lupus_v2.model.validatorStrategy.ValidatorStrategy
import com.example.lupus_v2.model.voteStrategy.MostVotedPlayer
import com.example.lupus_v2.model.voteStrategy.VoteResult
import com.example.lupus_v2.model.voteStrategy.VoteStrategy

sealed class RoleResultEvent(val roleTypes: List<RoleType>)


abstract class Role(
    val roleType: RoleType,
    val roleDescription: String = "Sono un ruolo!!!",
    @DrawableRes val image: Int,
    val color: Color,
    private val voteRoundExclusion: List<IntRange> = emptyList(),
    private val voteStrategy: VoteStrategy,
    private val validatorStrategy: ValidatorStrategy
) {

    fun getVoteStrategyCount(): Int {
        return voteStrategy.voteStrategyCount
    }

    fun canVote(currentRound: Int): Boolean {
        return voteRoundExclusion.none { currentRound in it }
    }

    /**
     * if the vote is valid, perform the vote
     * throw an IllegalStateException otherwise
     */
    fun vote(
        lastVotingState: RoleVotes,
        voter: PlayerDetails,
        votedPlayer: PlayerDetails
    ): VoteResult {
        validatorStrategy.validateVote(lastVotingState, voter, votedPlayer)

        return voteStrategy.vote(lastVotingState, voter, votedPlayer)
    }

    fun mostVotedPlayer(lastVotingState: RoleVotes): MostVotedPlayer {
        val mostVotedPlayer = voteStrategy.mostVotedPlayer(lastVotingState)

        if(mostVotedPlayer is MostVotedPlayer.Tie) {
            return voteStrategy.handleTie(mostVotedPlayer.players)
        }
        return voteStrategy.mostVotedPlayer(lastVotingState)
    }

    abstract fun roleRoundResult(mostVotedPlayers: Map<RoleType, MostVotedPlayer>): RoundResult

    // Protected helper method shared by subclasses for checking FaciliCostumi's save
    protected fun faciliCostumiSavedPlayer(votedPlayerByRole: Map<RoleType, MostVotedPlayer>): Boolean {
        return votedPlayerByRole[RoleType.Seduttrice] == votedPlayerByRole[RoleType.Assassino]
    }

    protected fun killedPlayerIsCupido(mostVotedPlayers: Map<RoleType, MostVotedPlayer>): Boolean {
        if (mostVotedPlayers.containsKey(RoleType.Cupido).not())
            return false

        val cupidoVotedPlayers = mostVotedPlayers[RoleType.Cupido] as MostVotedPlayer.PairPlayers
        val assassinVotedPlayer =
            mostVotedPlayers[RoleType.Assassino] as MostVotedPlayer.SinglePlayer
        val cupidoKilled =
            cupidoVotedPlayers.playerDetails1 == assassinVotedPlayer.playerDetails || cupidoVotedPlayers.playerDetails2 == assassinVotedPlayer.playerDetails
        return cupidoKilled
    }
}


/*
fun vote(lastVotingState: RoleVotes, voter: PlayerDetails, votedPlayer: PlayerDetails): VoteResult {
        if (!validatorStrategy.validateVote(lastVotingState, voter, votedPlayer)) {
            throw IllegalArgumentException("${validatorStrategy.types} : Invalid vote")
        }
        return voteStrategy.vote(lastVotingState, voter, votedPlayer)
    }
 */