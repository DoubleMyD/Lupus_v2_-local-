package com.example.lupus_v2.model.validatorStrategy

import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.manager.RoleVotes

class SelfVotingNotValid: ValidatorStrategy(
    types = listOf(ValidatorStrategyType.SelfVotingNotValid)
) {

    override fun validateVote(lastVotingState: RoleVotes, voter: PlayerDetails, votedPlayer: PlayerDetails): Boolean {
        if(voter != votedPlayer)
            return true
        throwValidationException(types.first())
    }

}