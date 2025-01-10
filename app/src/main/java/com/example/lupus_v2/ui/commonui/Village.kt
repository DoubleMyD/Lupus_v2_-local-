package com.example.lupus_v2.ui.commonui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.example.lupus_v2.data.di.KoinPreviewApplication
import com.example.lupus_v2.data.di.appModule
import com.example.lupus_v2.data.fake.FakePlayersRepository
import com.example.lupus_v2.model.PlayerDetails
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

@Composable
fun Village(
    modifier: Modifier = Modifier,
    players: List<PlayerDetails>,
    playersModifier: Modifier = Modifier,
    onOrderChanged: (List<Int>) -> Unit = {}, // Callback for order change (use the id)
    swipeEnabled: Boolean = true,
    onClicked: (PlayerDetails) -> Unit = {},
    content: @Composable (PlayerDetails) -> Unit
) {
    val boxCount = remember { players.size }
    // State to track the positions of the boxes
    val boxPositions = remember { mutableStateListOf<Pair<Float, Float>>() }
    val initialPositions = remember { mutableStateListOf<Pair<Float, Float>>() }
    val lastValidPositions = remember { mutableStateListOf<Pair<Float, Float>>() }
    val isDragging = remember { mutableStateOf(false) }
    val draggedIndex = remember { mutableIntStateOf(-1) }
    val dragOffset = remember { mutableStateOf(Offset(0f, 0f)) }

    /**
     *     Track the current order of items
     *     MutableMap< Index, Pair<Position, PlayerID> >
     */
    val currentOrder = remember {
        players.withIndex().associate { it.index to (it.index to it.value.id) }.toMutableMap()
    }


    // Box size (calculated outside of the Layout's measurePolicy)
    val minBoxSize = 128
    val maxBoxSize = 256
    val boxSize = remember { mutableFloatStateOf(0f) }

    Layout(
        modifier = modifier.padding(8.dp),
        content = {
            for (i in players.indices) {
                Box(
                    Modifier
                        .then(playersModifier)
//                        .then(
//                            if(i == 0) {
//                                Modifier.border(2.dp, androidx.compose.ui.graphics.Color.Red)
//                            } else Modifier
//                        )
                        .then(
                            if (swipeEnabled) {
                                Modifier.pointerInput(i) {
                                    detectDragGestures(
                                        onDragStart = {
                                            isDragging.value = true
                                            draggedIndex.intValue = i
                                            dragOffset.value = Offset(0f, 0f)
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            if (isDragging.value && draggedIndex.intValue == i) {
                                                dragOffset.value += Offset(
                                                    dragAmount.x,
                                                    dragAmount.y
                                                )
                                            }
                                        },
                                        onDragEnd = {
                                            isDragging.value = false
                                            val draggedBoxPosition =
                                                boxPositions[draggedIndex.intValue]
                                            val newCenter = Pair(
                                                draggedBoxPosition.first + dragOffset.value.x,
                                                draggedBoxPosition.second + dragOffset.value.y
                                            )
                                            val swapIndex = boxPositions.indexOfFirst {
                                                it != draggedBoxPosition && overlaps(
                                                    it,
                                                    newCenter,
                                                    boxSize.floatValue
                                                )
                                            }
                                            if (swapIndex != -1) {
                                                // Swap positions
                                                boxPositions.swap(draggedIndex.intValue, swapIndex)
                                                lastValidPositions.swap(
                                                    draggedIndex.intValue,
                                                    swapIndex
                                                )

                                                val draggedPlayerPosAndId =
                                                    currentOrder[draggedIndex.intValue]
                                                val swappedPlayerPosAndId = currentOrder[swapIndex]

                                                currentOrder[draggedIndex.intValue] =
                                                    draggedPlayerPosAndId!!.copy(first = swappedPlayerPosAndId!!.first)
                                                currentOrder[swapIndex] =
                                                    swappedPlayerPosAndId.copy(first = draggedPlayerPosAndId.first)

                                                val newOrder =
                                                    currentOrder.values
                                                        .sortedBy { it.first }
                                                        .map { it.second }
                                                onOrderChanged(newOrder)
                                            } else {
                                                // Reset position if out of bounds
                                                boxPositions[draggedIndex.intValue] =
                                                    lastValidPositions[draggedIndex.intValue]
                                            }
                                            draggedIndex.intValue = -1
                                            dragOffset.value =
                                                Offset(0f, 0f) // Reset the drag offset
                                        }
                                    )
                                }
                            } else Modifier
                        )
                        .pointerInput(i) {
                            detectTapGestures(
                                onTap = {
                                    if (!swipeEnabled || draggedIndex.intValue == -1) {
                                        onClicked(players[i])
                                    }
                                }
                            )
                        }
                ) {
                    content(players[i])
                }
            }
        },
        measurePolicy = { measurables, constraints ->
            // Calculate boxSize based on constraints
            val totalAvailableHeight = constraints.maxHeight
            val totalPadding = boxCount * 4 // Gap between boxes
            val remainingHeight = totalAvailableHeight - totalPadding
            val idealBoxSize = remainingHeight / (boxCount / 2 + 2)
            var calculatedBoxSize = idealBoxSize.coerceIn(minBoxSize, maxBoxSize)

            // Set box size to remember
            boxSize.floatValue = calculatedBoxSize.toFloat()

            val padding = calculatedBoxSize / 2
            val availableWidth = constraints.maxWidth - 2 * padding
            val availableHeight = constraints.maxHeight - 2 * padding

            // Ellipse dimensions+
            var a = availableWidth / 2f // Horizontal radius
            var b = availableHeight / 2f // Vertical radius
            val centerX = constraints.maxWidth / 2f
            val centerY = constraints.maxHeight / 2f

            // Initialize positions if not already done
            if (boxPositions.isEmpty()) {
                initialPositions.addAll((0 until boxCount).map { i ->
                    var t = (2 * Math.PI * i / boxCount).toFloat()
                    t += 0.3f * atan(((a - b) * tan(t)) / (a + b * tan(t).pow(2)))
                    val sq = 1.0 / hypot(a * sin(t), b * cos(t))
                    val x = (a - b * sq) * cos(t) + centerX
                    val y = (b - a * sq) * sin(t) + centerY
                    x.toFloat() to y.toFloat()
                })
                boxPositions.addAll(initialPositions)
                lastValidPositions.addAll(initialPositions)
            }

            var resolvedBoxPosition = resolveOverlaps(boxPositions, (maxBoxSize.toFloat()) + 32)
            var index = 0
            while (index < 10 && resolvedBoxPosition.any {
                    it.first < 0 || it.second < 0
                            || (it.first + calculatedBoxSize) > constraints.maxWidth || (it.second + calculatedBoxSize) > constraints.maxHeight
                }) {
                calculatedBoxSize -= 2
                a -= 16
                b -= 16
                boxSize.floatValue = calculatedBoxSize.toFloat()

                resolvedBoxPosition =
                    resolveOverlaps(boxPositions, (calculatedBoxSize.toFloat()) + index * 6)
                index++
            }


            // Measure boxes
            val placeables = measurables.map { measurable ->
                measurable.measure(
                    Constraints.fixed(calculatedBoxSize, calculatedBoxSize)
                )
            }

            layout(constraints.maxWidth, constraints.maxHeight) {
                resolvedBoxPosition.forEachIndexed { index, (x, y) ->
                    val offsetX = if (index == draggedIndex.intValue) dragOffset.value.x else 0f
                    val offsetY = if (index == draggedIndex.intValue) dragOffset.value.y else 0f

                    // Ensure box stays within the bounds of the screen
                    val constrainedX = (x + offsetX).coerceIn(
                        0f,
                        (constraints.maxWidth).toFloat()
                    )
                    val constrainedY = (y + offsetY).coerceIn(
                        0f,
                        (constraints.maxHeight).toFloat()
                    )

                    placeables[index].place(
                        x = (constrainedX - calculatedBoxSize / 2).toInt(),
                        y = (constrainedY - calculatedBoxSize / 2).toInt()
                    )
                }
            }
        }
    )
}

// Helper function to check overlap
private fun overlaps(pos1: Pair<Float, Float>, pos2: Pair<Float, Float>, boxSize: Float): Boolean {
    val distance = sqrt((pos1.first - pos2.first).pow(2) + (pos1.second - pos2.second).pow(2))
    return distance < boxSize
}

// Helper function to swap elements in a mutable list
private fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val temp = this[index1]
    this[index1] = this[index2]
    this[index2] = temp
}


// 1 version!!! Helper function to resolve overlaps
private fun resolveOverlaps(
    positions: List<Pair<Float, Float>>,
    boxSize: Float
): List<Pair<Float, Float>> {
    val adjustedPositions = positions.toMutableList()
    val radius = boxSize / 2
    var hasOverlap = true
    val maxIterations = 10
    var iteration = 0

    while (hasOverlap && iteration < maxIterations) {
        hasOverlap = false
        for (i in adjustedPositions.indices) {
            for (j in i + 1 until adjustedPositions.size) {
                val (x1, y1) = adjustedPositions[i]
                val (x2, y2) = adjustedPositions[j]
                val distance = hypot(x2 - x1, y2 - y1)

                if (distance < radius * 2) {
                    hasOverlap = true
                    // Move boxes apart
                    val overlap = radius * 2 - distance
                    val angle = atan2(y2 - y1, x2 - x1)
                    adjustedPositions[i] =
                        x1 - overlap / 2 * cos(angle) to y1 - overlap / 2 * sin(angle)
                    adjustedPositions[j] =
                        x2 + overlap / 2 * cos(angle) to y2 + overlap / 2 * sin(angle)
                }
            }
        }
        iteration++
    }

    return adjustedPositions
}


@Preview(showBackground = true)
@Composable
fun VillagePreview() {
    KoinPreviewApplication(
        modules = { listOf(appModule) }
    ) {
        val players = FakePlayersRepository.playerDetails//.subList(0, 18)
        Village(
            players = players,
            modifier = Modifier.fillMaxSize()
        ) { player ->
            PlayerImage(
                imageSource = player.imageSource,
                padding = 0,
                modifier = Modifier.fillMaxSize()//.size(dimensionResource(id = R.dimen.img_big))
            )
        }
    }
}


/*
* for (i in players.indices) {
                Box(
                    Modifier
//                        .then(
//                            if(i == 0) {
//                                Modifier.border(2.dp, androidx.compose.ui.graphics.Color.Red)
//                            } else Modifier
//                        )
                        .then(
                            if (swipeEnabled) {
                                Modifier.pointerInput(i) {
                                    detectDragGestures(
                                        onDragStart = {
                                            isDragging.value = true
                                            draggedIndex.intValue = i
                                            dragOffset.value = Offset(0f, 0f)
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            if (isDragging.value && draggedIndex.intValue == i) {
                                                dragOffset.value += Offset(
                                                    dragAmount.x,
                                                    dragAmount.y
                                                )
                                            }
                                        },
                                        onDragEnd = {
                                            isDragging.value = false
                                            val draggedBoxPosition =
                                                boxPositions[draggedIndex.intValue]
                                            val newCenter = Pair(
                                                draggedBoxPosition.first + dragOffset.value.x,
                                                draggedBoxPosition.second + dragOffset.value.y
                                            )
                                            val swapIndex = boxPositions.indexOfFirst {
                                                it != draggedBoxPosition && overlaps(
                                                    it,
                                                    newCenter,
                                                    boxSize.floatValue
                                                )
                                            }
                                            if (swapIndex != -1) {
                                                Log.d("DragEnd", "DRAGGED_INDEX: ${draggedIndex.intValue}, swapIndex: $swapIndex ")
                                                // Swap positions
                                                boxPositions.swap(draggedIndex.intValue, swapIndex)
                                                lastValidPositions.swap(
                                                    draggedIndex.intValue,
                                                    swapIndex
                                                )
                                                Log.d("DragEnd", "currentOrder: $currentOrder")
                                                // Update the current order and notify the parent
                                                val temp1 = currentOrder.indexOf(draggedIndex.intValue)
                                                val temp2 = currentOrder.indexOf(swapIndex)
                                                currentOrder[temp1] = draggedIndex.intValue
                                                currentOrder[temp2] = swapIndex

                                                //currentOrder.swap(draggedIndex.intValue, swapIndex)
                                                Log.d("DragEnd", "orderOnUpdate: $currentOrder")

                                                onOrderChanged(currentOrder.toList())
                                            } else {
                                                // Reset position if out of bounds
                                                boxPositions[draggedIndex.intValue] =
                                                    lastValidPositions[draggedIndex.intValue]
                                            }
                                            draggedIndex.intValue = -1
                                            dragOffset.value =
                                                Offset(0f, 0f) // Reset the drag offset
                                        }
                                    )
                                }
                            } else Modifier
                        )
                        .pointerInput(i) {
                            detectTapGestures(
                                onTap = {
                                    if (!swipeEnabled || draggedIndex.intValue == -1) {
                                        onClicked(players[i])
                                    }
                                }
                            )
                        }
                ) {
                        content(players[i])
                    if(boxPositions.isNotEmpty()) {
                        Text(i.toString(), fontSize = 32.sp,  modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                    }
                }
            }
* */