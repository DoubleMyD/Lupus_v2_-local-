package com.example.lupus_v2.model.validatorStrategy

import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.manager.RoleVotes


class SameRoleNotValid : ValidatorStrategy(
    types = listOf(ValidatorStrategyType.SameRoleNotValid)
) {

    override fun validateVote(lastVotingState: RoleVotes, voter: PlayerDetails, votedPlayer: PlayerDetails): Boolean {
        if(voter.role.roleType != votedPlayer.role.roleType)
            return true
        throwValidationException(types.first(), extra = " voter: $voter / votedPlayer: $votedPlayer")
    }
}