package com.example.lupus_v2.model.roles

import androidx.compose.ui.graphics.Color
import com.example.lupus_v2.R
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.manager.PlayerManager
import com.example.lupus_v2.model.manager.RoundResult
import com.example.lupus_v2.model.validatorStrategy.ValidatorFactoryDeadPlayerNotValid
import com.example.lupus_v2.model.voteStrategy.MostVotedPlayer
import com.example.lupus_v2.model.voteStrategy.OneVote

data class VeggenteDiscoverKiller(val killer: PlayerDetails) : RoleResultEvent(roleTypes = listOf(RoleType.Veggente))


class Veggente(
    private val playerManager: PlayerManager
) : Role(
    roleType = RoleType.Veggente,
    image = R.drawable.veggente,
    color = Color.Green,
    voteStrategy = OneVote(),
    validatorStrategy = ValidatorFactoryDeadPlayerNotValid.getValidatorSelfVotingSameRole()
) {
    override fun roleRoundResult(mostVotedPlayers: Map<RoleType, MostVotedPlayer>): RoundResult {
        val veggenteVotedPlayer = mostVotedPlayers[RoleType.Veggente] as MostVotedPlayer.SinglePlayer
        val assassinVotedPlayer = mostVotedPlayers[RoleType.Assassino] as MostVotedPlayer.SinglePlayer

        // If the Veggente didn’t vote for the Assassino, or no valid votes are found, Veggente discovers nothing
        if (veggenteVotedPlayer.playerDetails.role.roleType != RoleType.Assassino) {
            return RoundResult(emptyList(), emptyList())
        }

        return if (isVeggenteAlive(assassinVotedPlayer.playerDetails)) {
            RoundResult(listOf(VeggenteDiscoverKiller(veggenteVotedPlayer.playerDetails)), emptyList())
        } else {
            RoundResult(emptyList(), emptyList())
        }

    }

    // Helper function to check if any Veggente is alive other than the one targeted by the assassin
    //if the player passed is a veggente, is included in the check, if is not a veggente, is excluded
    private fun isVeggenteAlive(assassinTarget: PlayerDetails): Boolean {
        return playerManager.players.value.any {
            it.role.roleType == RoleType.Veggente && it.id != assassinTarget.id
        }
    }
}