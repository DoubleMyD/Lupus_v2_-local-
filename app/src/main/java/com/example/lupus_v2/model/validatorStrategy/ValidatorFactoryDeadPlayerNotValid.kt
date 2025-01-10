package com.example.lupus_v2.model.validatorStrategy

object ValidatorFactoryDeadPlayerNotValid {

    private val commonValidator: List<ValidatorStrategy> = listOf(
        DeadPlayerNotValid()
    )

    fun getValidatorSelfVotingSameRole(): ValidatorStrategy{
        return CombinedVoteValidator(
            validators = commonValidator + listOf(
                SelfVotingNotValid(),
                SameRoleNotValid()
            )
        )
    }

    fun getValidatorSameRole(): ValidatorStrategy{
        return CombinedVoteValidator(
            validators = commonValidator + listOf(
                SameRoleNotValid()
            )
        )
    }

    fun getValidatorDuplicateVote(): ValidatorStrategy{
        return CombinedVoteValidator(
            validators = commonValidator + listOf(
                DuplicateVoteNotValid()
            )
        )
    }

    fun getValidatorSelfVoting(): ValidatorStrategy{
        return CombinedVoteValidator(
            validators = commonValidator + listOf(
                SelfVotingNotValid()
            )
        )
    }

    fun getValidatorOneTimeSelfVoting(): ValidatorStrategy{
        return CombinedVoteValidator(
            validators = commonValidator + listOf(
                OneTimeSelfVoting()
            )
        )
    }

}