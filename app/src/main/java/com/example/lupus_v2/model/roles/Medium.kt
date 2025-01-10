package com.example.lupus_v2.model.roles

import androidx.compose.ui.graphics.Color
import com.example.lupus_v2.R
import com.example.lupus_v2.model.manager.PlayerManager
import com.example.lupus_v2.model.manager.RoundResult
import com.example.lupus_v2.model.validatorStrategy.ValidatorFactoryDeadPlayerNotValid
import com.example.lupus_v2.model.voteStrategy.CopyRole
import com.example.lupus_v2.model.voteStrategy.MostVotedPlayer

class Medium(
    private val playerManager: PlayerManager
) : Role(
    roleType = RoleType.Medium,
    image = R.drawable.medium,
    color = Color.Green,
    voteRoundExclusion = listOf(1..1,3..100),
    voteStrategy = CopyRole(playerManager),
    validatorStrategy = ValidatorFactoryDeadPlayerNotValid.getValidatorSelfVotingSameRole()
) {
    override fun roleRoundResult(mostVotedPlayers: Map<RoleType, MostVotedPlayer>): RoundResult {
        return RoundResult(emptyList(), emptyList())
    }

}