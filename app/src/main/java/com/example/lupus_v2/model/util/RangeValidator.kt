package com.example.lupus_v2.model.util

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ValidRange(
    val startRange: Int = 1,
    val finishRange: Int = 10,
    val minAllowedValue: Int = 1,
    val maxAllowedValue: Int = finishRange
)

class RangeValidator() {
    private val _validRangeState = MutableStateFlow(ValidRange())
    val validRangeState: StateFlow<ValidRange> = _validRangeState.asStateFlow()

    fun updateMaxAndMinAllowedValue(maxSize: Int, selectedValues: Int, currentValue: Int) {
        val remainingPlayers = maxSize - selectedValues
        val maxAllowedValue =
            (remainingPlayers + currentValue).coerceAtMost(_validRangeState.value.finishRange)

        _validRangeState.update { currentState ->
            currentState.copy(minAllowedValue = 1, maxAllowedValue = maxAllowedValue)
        }
        Log.d("RangeValidator", "updateMaxAndMinAllowedValue: ${_validRangeState.value}")
    }

    fun checkValue(newValue: Float): Boolean {
        return newValue.toInt() < _validRangeState.value.maxAllowedValue
    }

    fun getValidValue(newValue: Float): Float {
        return newValue.coerceIn(_validRangeState.value.minAllowedValue.toFloat(), _validRangeState.value.maxAllowedValue.toFloat())

    }
}