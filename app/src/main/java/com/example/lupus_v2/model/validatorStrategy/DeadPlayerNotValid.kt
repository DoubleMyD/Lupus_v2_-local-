package com.example.lupus_v2.model.validatorStrategy

import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.manager.RoleVotes

class DeadPlayerNotValid : ValidatorStrategy(
    types = listOf(ValidatorStrategyType.DeadPlayerNotValid)
) {

    /**
     * return true if the player is alive, false otherwise
     */
    override fun validateVote(lastVotingState: RoleVotes, voter: PlayerDetails, votedPlayer: PlayerDetails): Boolean {
        if(votedPlayer.alive)
            return true
        throwValidationException(types.first())
    }

}