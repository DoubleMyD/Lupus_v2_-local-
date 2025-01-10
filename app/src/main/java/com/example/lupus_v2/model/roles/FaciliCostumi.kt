package com.example.lupus_v2.model.roles

import androidx.compose.ui.graphics.Color
import com.example.lupus_v2.R
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.manager.RoundResult
import com.example.lupus_v2.model.validatorStrategy.ValidatorFactoryDeadPlayerNotValid
import com.example.lupus_v2.model.voteStrategy.MostVotedPlayer
import com.example.lupus_v2.model.voteStrategy.OneVote

data class SeduttriceSavedPlayer(val playerSaved: PlayerDetails) : RoleResultEvent(roleTypes = listOf(RoleType.Seduttrice))

class FaciliCostumi : Role(
    roleType = RoleType.Seduttrice,
    image = R.drawable.seduttrice,
    color = Color.Green,
    voteStrategy = OneVote(),
    validatorStrategy = ValidatorFactoryDeadPlayerNotValid.getValidatorOneTimeSelfVoting()
) {

    /**
     * if had saved a player,
     * return a roleResultEvent containing the player that has been saved
     */
    override fun roleRoundResult(mostVotedPlayers: Map<RoleType, MostVotedPlayer>): RoundResult {
        if(super.faciliCostumiSavedPlayer(mostVotedPlayers)){
            val savedPlayer = mostVotedPlayers[RoleType.Seduttrice] as MostVotedPlayer.SinglePlayer
            return RoundResult(listOf( SeduttriceSavedPlayer(savedPlayer.playerDetails) ), emptyList())
        } else{
            return RoundResult(emptyList(), emptyList())
        }
    }

}