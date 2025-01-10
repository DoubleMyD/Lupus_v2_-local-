package com.example.lupus_v2.model.roles

import androidx.compose.ui.graphics.Color
import com.example.lupus_v2.R
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.manager.RoundResult
import com.example.lupus_v2.model.validatorStrategy.DeadPlayerNotValid
import com.example.lupus_v2.model.voteStrategy.MostVotedPlayer
import com.example.lupus_v2.model.voteStrategy.OneVote

data class CitizienKilledPlayers(val playerKilled: PlayerDetails) : RoleResultEvent(roleTypes = listOf(RoleType.Cittadino))

class Cittadino() : Role(
    roleType = RoleType.Cittadino,
    image = R.drawable.farmer,
    color = Color.Green,
    voteStrategy = OneVote(),
    validatorStrategy = DeadPlayerNotValid()
){
    override fun roleRoundResult(mostVotedPlayers: Map<RoleType, MostVotedPlayer>): RoundResult{

        val mostVotedPlayer = mostVotedPlayers[RoleType.Cittadino] as MostVotedPlayer.SinglePlayer
        return RoundResult(
            listOf(CitizienKilledPlayers(mostVotedPlayer.playerDetails)),
            listOf(mostVotedPlayer.playerDetails.kill())
        )

    }
}