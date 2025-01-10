package com.example.lupus_v2.ui.screens.game.game_prepare.choose_role

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.lupus_v2.model.manager.GameManager
import com.example.lupus_v2.model.manager.GameRules
import com.example.lupus_v2.model.roles.RoleType
import com.example.lupus_v2.model.util.Randomizer
import com.example.lupus_v2.model.util.RangeValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChooseRoleViewModel(
    private val gameManager: GameManager,
    private val rangeValidator: RangeValidator,
    private val randomizer: Randomizer
) : ViewModel() {
    // Store the last known state for Random and Manual
    private var savedRandomState = ChooseRoleUiState.Random()
    private var savedManualState = ChooseRoleUiState.Manual()


    private val _uiState = MutableStateFlow<ChooseRoleUiState>(savedManualState)
    val uiState: StateFlow<ChooseRoleUiState> = _uiState.asStateFlow()

    private val playerSize
        get() = gameManager.gameState.value.playerManager.players.value.size

    //    private val roleCounts
//        get() = (uiState.value as ChooseRoleUiState.Manual).roleCount.values.sum()
    private val remainingPlayers
        get() = (playerSize) - (_uiState.value as ChooseRoleUiState.Manual).roleCount.values.sum()

    init {
        savedManualState = ChooseRoleUiState.Manual(
            roleCount = GameRules.ESSENTIAL_ROLES.associateWith { 1 },
            remainingPlayers = playerSize - GameRules.ESSENTIAL_ROLES.size
        )
        savedRandomState = ChooseRoleUiState.Random(roleSelected = GameRules.ESSENTIAL_ROLES)

        _uiState.update { currentState ->
            when (currentState) {
                is ChooseRoleUiState.Manual -> {
                    currentState.copy(
                        roleCount = GameRules.ESSENTIAL_ROLES.associateWith { 1 },
                        remainingPlayers = playerSize - GameRules.ESSENTIAL_ROLES.size
                    )
                }

                //this is not executed, the default method is Manual, not Random
                is ChooseRoleUiState.Random -> {
                    currentState.copy(
                        roleSelected = GameRules.ESSENTIAL_ROLES
                    )
                }
            }
        }
    }

    fun confirmRoles(): Boolean {
        when (_uiState.value) {
            is ChooseRoleUiState.Manual -> {
                val playerSizeOk =
                    gameManager.initRolePlayerCount((_uiState.value as ChooseRoleUiState.Manual).roleCount)
                return playerSizeOk
            }

            is ChooseRoleUiState.Random -> {
                val updatedPlayersForRole = randomizer.randomize(
                    startingValues = (uiState.value as ChooseRoleUiState.Random).roleSelected.associateWith { 1 },
                    idealDistribution = gameManager.getFilteredDistribution((uiState.value as ChooseRoleUiState.Random).roleSelected),
                    remainingPlaces = playerSize,
                    maxValue = rangeValidator.validRangeState.value.finishRange
                )
                val playerSizeOk = gameManager.initRolePlayerCount(updatedPlayersForRole)
                return playerSizeOk
            }
        }
    }

    // Switch between states, retaining data in each mode
    fun switchUiState(isRandom: Boolean) {
        _uiState.value = if (isRandom) {
            // Save the current Manual state before switching
            if (_uiState.value is ChooseRoleUiState.Manual) {
                savedManualState = _uiState.value as ChooseRoleUiState.Manual
            }
            savedRandomState // Set state to Random (restored from savedRandomState)
        } else {
            // Save the current Random state before switching
            if (_uiState.value is ChooseRoleUiState.Random) {
                savedRandomState = _uiState.value as ChooseRoleUiState.Random
            }
            savedManualState // Set state to Manual (restored from savedManualState)
        }
    }

    fun incrementCount(roleType: RoleType) {
        _uiState.update { currentState ->
            when (currentState) {
                is ChooseRoleUiState.Manual -> {
                    val updatedCount = currentState.roleCount.toMutableMap().apply {
                        val newValue = (this[roleType] ?: 0) + 1
                        if ((remainingPlayers) > 0) {
                            this[roleType] = newValue
                        }
                    }
                    val updatedRemainingPlayers = playerSize - updatedCount.values.sum()
                    currentState.copy(
                        roleCount = updatedCount,
                        remainingPlayers = updatedRemainingPlayers,
                    ).also { savedManualState = it }
                }

                is ChooseRoleUiState.Random -> currentState // No change in Random state
            }
        }
    }

    // Function to decrement the role count in the Manual state
    fun decrementCount(roleType: RoleType) {
        _uiState.update { currentState ->
            when (currentState) {
                is ChooseRoleUiState.Manual -> {
                    val updatedCount = currentState.roleCount.toMutableMap().apply {
                        val newValue = (this[roleType] ?: 1) - 1
                        if (GameRules.ESSENTIAL_ROLES.contains(roleType)) {
                            if (newValue > 0) {
                                this[roleType] = newValue
                            }
                        } else {
                            this[roleType] = newValue
                        }
                    }.filterValues { it > 0 }  // Remove roles with count 0

                    val updatedRemainingPlayers = playerSize - updatedCount.values.sum()
                    currentState.copy(
                        roleCount = updatedCount,
                        remainingPlayers = updatedRemainingPlayers
                    ).also { savedManualState = it }
                }

                is ChooseRoleUiState.Random -> currentState // No change in Random state
            }
        }
    }

    fun toggleRoleSelected(roleType: RoleType) {
        if (GameRules.ESSENTIAL_ROLES.contains(roleType))
            return

        _uiState.update { currentState ->
            when (currentState) {
                is ChooseRoleUiState.Manual -> {
                    currentState
                }

                is ChooseRoleUiState.Random -> {
                    if (currentState.roleSelected.contains(roleType)) {
                        currentState.copy(
                            roleSelected = currentState.roleSelected - roleType
                        )
                    } else {
                        currentState.copy(
                            roleSelected = currentState.roleSelected + roleType
                        )
                    }
                }
            }
        }
    }


}