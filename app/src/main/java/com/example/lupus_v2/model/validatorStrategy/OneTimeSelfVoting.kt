package com.example.lupus_v2.model.validatorStrategy

import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.manager.RoleVotes

class OneTimeSelfVoting : ValidatorStrategy(
    types = listOf(ValidatorStrategyType.OneTimeSelfVoting)
) {
    //TODO probably need to create a list of player where to check if already saved himself
    private var hasAlreadyVotedItSelf: Boolean = false

    override fun validateVote(lastVotingState: RoleVotes, voter: PlayerDetails, votedPlayer: PlayerDetails): Boolean {
        if(voter != votedPlayer) return true

        if (hasAlreadyVotedItSelf)
            throwValidationException(types.first())

        hasAlreadyVotedItSelf = true
        return true
    }
}