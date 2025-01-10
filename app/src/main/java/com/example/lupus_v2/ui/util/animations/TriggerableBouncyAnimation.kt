package com.example.lupus_v2.ui.util.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun TriggerableBouncyAnimation(
    isBouncing: Boolean,
    onAnimationEnd: () -> Unit,
    bounceDuration: Int = 250,
    totalDuration: Long = 2000,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Track the vertical offset for the bounce animation
    val bounceOffset = remember { Animatable(0f) }

    LaunchedEffect(key1 = isBouncing) {
        if (isBouncing) {
            // Trigger the bounce animation
            bounceOffset.animateTo(
                targetValue = -20f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = bounceDuration, // Up-and-down duration
                        easing = LinearOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            bounceOffset.snapTo(0f) // Reset offset when not bouncing
        }
    }

    // Stop the animation after 2 seconds
    LaunchedEffect(isBouncing) {
        if (isBouncing) {
            delay(totalDuration) // Wait for 2 seconds
            onAnimationEnd() // Reset the trigger
        }
    }

    Box(
        modifier = modifier.offset { IntOffset(0, bounceOffset.value.roundToInt()) },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}