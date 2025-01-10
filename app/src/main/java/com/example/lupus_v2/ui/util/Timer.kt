package com.example.lupus_v2.ui.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.lupus_v2.R
import com.example.lupus_v2.ui.commonui.CancelAndConfirmButtons
import kotlinx.coroutines.delay

/**
 * Timer composable with integrated skip timer logic (using a dialog)
 *
 * @param timerKey is used to control when to reset the timer. Default is "i do not restart"
 */
@Composable
fun TimerSection(
    timerKey: Any = "i do not restart", //is used to control when to reset the timer
    skipTimerDialogText: String,
    modifier: Modifier = Modifier,
    enableSkip : Boolean,
    circleRadius: Int = 128,
    showLeftTime: Boolean = true,
    timerDuration: Int = 30,
    onTimerFinished: () -> Unit = {},
    onTimerUpdate: () -> Unit = {},
    ) {
    val timer = remember(timerKey) { Timer() }
    var showAlert by remember { mutableStateOf(false) }

    timer.Circular(
        key = timerKey,
        showLeftTime = showLeftTime,
        circleRadius = circleRadius,
        maxTime = timerDuration,
        onTimerFinished = { onTimerFinished() },
        onTimerUpdate = onTimerUpdate,
        modifier = modifier.clickable { showAlert = true },
    )

    if (showAlert && enableSkip) {
        timer.SkipTimerDialog(
            onSkipTimer = {
                showAlert = false
                onTimerFinished()
            },
            onDismissDialog = { showAlert = false },
            dialogText = skipTimerDialogText
        )
    }
}


class Timer() {

    // Shared timer logic
    @Composable
    private fun TimerLogic(
        key: Any,
        maxTime: Int,
        onTimerFinished: () -> Unit,
        onTimerUpdate: () -> Unit = {},
        content: @Composable (timeLeft: Int) -> Unit
    ) {
        var timeLeft by remember { mutableIntStateOf(maxTime) }
        val updatedOnTimerFinished by rememberUpdatedState(onTimerFinished)
        val updatedOnTimerUpdate by rememberUpdatedState(onTimerUpdate)

        LaunchedEffect(key) {
            timeLeft = maxTime
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
                onTimerUpdate()
            }
            if (timeLeft == 0) {
                delay(750L)
                onTimerFinished()
            }
        }

        content(timeLeft)
    }

    @Composable
    fun SkipTimerDialog(
        dialogText: String,
        onSkipTimer: () -> Unit,
        onDismissDialog: () -> Unit = {},
        modifier: Modifier = Modifier
            .size(300.dp, 150.dp)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
    ){
        Dialog(
            onDismissRequest = { onDismissDialog() }
        ) {
            ElevatedCard(
                modifier = modifier
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                ) {
                    Text(
                        text = dialogText,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .weight(0.6f)
                            .padding(dimensionResource(id = R.dimen.padding_small))
                    )
                    CancelAndConfirmButtons(
                        onConfirmClick = { onSkipTimer() },
                        onCancelClick = { onDismissDialog() },
                        modifier = Modifier
                            .weight(0.4f)
                            .padding(dimensionResource(id = R.dimen.padding_small))
                    )
                }
            }
        }
    }

    // Column Timer
    @Composable
    fun Column(
        key: Any,
        modifier: Modifier = Modifier,
        maxTime: Int = 30,
        columnWidth: Int = 32,
        showLeftTime: Boolean = true,
        onTimerFinished: () -> Unit = {},
        onTimerUpdate: () -> Unit = {},
    ) {
        TimerLogic(
            key = key,
            maxTime = maxTime,
            onTimerFinished = onTimerFinished,
            onTimerUpdate = onTimerUpdate
        ) { timeLeft ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Gray),
                    modifier = Modifier
                        .size(width = columnWidth.dp, height = (timeLeft * 10).dp)
                ) {
                    if (showLeftTime) {
                        Text(
                            text = timeLeft.toString(),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }

    // Circular Timer
    @Composable
    fun Circular(
        key: Any,
        modifier: Modifier = Modifier,
        maxTime: Int = 30,
        circleRadius: Int = 32,
        showLeftTime: Boolean = true,
        onTimerFinished: () -> Unit = {},
        onTimerUpdate: () -> Unit = {},
    ) {
        TimerLogic(
            key = key,
            maxTime = maxTime,
            onTimerFinished = onTimerFinished,
            onTimerUpdate = onTimerUpdate
        ) { timeLeft ->
            val animatedProgress by animateFloatAsState(
                targetValue = timeLeft.toFloat() / maxTime,
                animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                label = "Circular Timer Progress"
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
            ) {
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .size(circleRadius.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    //trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                )
                if (showLeftTime) {
                    Text(
                        text = timeLeft.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}