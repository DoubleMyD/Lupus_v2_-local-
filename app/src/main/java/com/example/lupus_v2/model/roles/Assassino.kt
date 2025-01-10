package com.example.lupus_v2.model.roles

import androidx.compose.ui.graphics.Color
import com.example.lupus_v2.R
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.manager.RoundResult
import com.example.lupus_v2.model.validatorStrategy.ValidatorFactoryDeadPlayerNotValid
import com.example.lupus_v2.model.voteStrategy.MostVotedPlayer
import com.example.lupus_v2.model.voteStrategy.OneVote

data class AssassinKilledPlayers(val playerKilled: PlayerDetails) : RoleResultEvent(roleTypes = listOf(RoleType.Assassino))
data class AssassinKilledPlayerIsCupido(val playersKilled: Pair<PlayerDetails, PlayerDetails>) :
    RoleResultEvent(roleTypes = listOf(RoleType.Assassino, RoleType.Cupido))

class Assassino : Role(
    roleType = RoleType.Assassino,
    image = R.drawable.assassino,
    color = Color.Red,
    voteStrategy = OneVote(),
    validatorStrategy = ValidatorFactoryDeadPlayerNotValid.getValidatorSameRole()
) {

    override fun roleRoundResult(mostVotedPlayers: Map<RoleType, MostVotedPlayer>): RoundResult {
        val mostVotedPlayer = mostVotedPlayers[RoleType.Assassino] as MostVotedPlayer.SinglePlayer

        if (super.faciliCostumiSavedPlayer(mostVotedPlayers)) {
            return RoundResult(emptyList(), emptyList())
        } else {
            if (super.killedPlayerIsCupido(mostVotedPlayers)) {
                val cupidoVotedPlayers =
                    mostVotedPlayers[RoleType.Cupido] as MostVotedPlayer.PairPlayers
                return RoundResult(
                    listOf(
                        AssassinKilledPlayerIsCupido(
                            Pair(
                                cupidoVotedPlayers.playerDetails1,
                                cupidoVotedPlayers.playerDetails2
                            )
                        )
                    ),
                    listOf(
                        cupidoVotedPlayers.playerDetails1.kill(),
                        cupidoVotedPlayers.playerDetails2.kill()
                    )
                )
            } else {
                return RoundResult(
                    listOf(AssassinKilledPlayers(mostVotedPlayer.playerDetails)),
                    listOf(mostVotedPlayer.playerDetails.kill())
                )
            }
        }
    }

}