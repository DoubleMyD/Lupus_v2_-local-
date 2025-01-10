package com.example.lupus_v2.model.validatorStrategy

import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.manager.RoleVotes
import java.lang.Exception

enum class ValidatorStrategyType(val exceptionMessage: String) {
    DeadPlayerNotValid("Dead players are not valid"),
    DuplicateVoteNotValid("Duplicate votes are not valid"),
    OneTimeSelfVoting("Self voting is allowed only once"),
    SameRoleNotValid("Same role voting is not valid"),
    SelfVotingNotValid("Self voting is not valid");

    fun getException(extra: String = ""): Exception = IllegalStateException(exceptionMessage + extra)
}

abstract class ValidatorStrategy(
    val types: List<ValidatorStrategyType>
) {
    /**
     * return true if the vote is valid, false otherwise
     */
    abstract fun validateVote(lastVotingState: RoleVotes, voter: PlayerDetails, votedPlayer: PlayerDetails): Boolean

    /**
     * Utility function to throw the appropriate exception for a given strategy type.
     */
    protected fun throwValidationException(type: ValidatorStrategyType, extra: String = ""): Nothing {
        throw type.getException(extra)
    }
}