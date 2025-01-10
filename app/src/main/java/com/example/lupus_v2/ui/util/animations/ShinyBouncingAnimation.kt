package com.example.lupus_v2.ui.util.animations

import androidx.compose.animation.core.EaseInCirc
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned

@Composable
fun ShinyBouncingAnimation(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val gradientWidthFraction = 0.5f
    val animationDuration = 3000
    // Bounce Animation
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween((animationDuration * 0.8).toInt(), easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ), label = "scaling effect"
    )

    // Transition for shimmer animation
    val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(delayMillis = 500, durationMillis = animationDuration, easing = EaseInCirc),
            repeatMode = RepeatMode.Reverse
        ), label = "shimmerEffect"
    )

    val animatedGradientWidthFraction by infiniteTransition.animateFloat(
        initialValue = 0.2f, // Start with a smaller gradient width
        targetValue = gradientWidthFraction, // Animate to the desired width
        animationSpec = infiniteRepeatable(
            animation = tween(delayMillis = 500, durationMillis = animationDuration, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient width animation"
    )


    // Size provider for measuring the content
    var contentWidth by remember { mutableIntStateOf(0) }
    var contentHeight by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates ->
                contentWidth = layoutCoordinates.size.width
                contentHeight = layoutCoordinates.size.height
            }
            .then( if (isActive) Modifier.graphicsLayer(scaleX = scale, scaleY = scale) else Modifier )
    ) {
        content()

        if (isActive && contentWidth > 0 && contentHeight > 0) {
            // Calculate gradient start and end based on animated width
            val gradientWidth = contentWidth * animatedGradientWidthFraction

            // Create a dynamic gradient based on content size
            val shimmerBrush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(0.25f),
                    Color.Transparent,
                ),
                start = Offset(shimmerProgress * contentWidth - gradientWidth / 2, 0f),
                end = Offset(shimmerProgress * contentWidth + gradientWidth / 2, 0f)
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(shimmerBrush, shape = MaterialTheme.shapes.extraLarge)
            )
        }
    }
}
