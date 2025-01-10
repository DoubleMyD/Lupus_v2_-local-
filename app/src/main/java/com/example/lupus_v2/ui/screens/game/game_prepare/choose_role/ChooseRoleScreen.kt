package com.example.lupus_v2.ui.screens.game.game_prepare.choose_role

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lupus_v2.R
import com.example.lupus_v2.data.di.KoinPreviewApplication
import com.example.lupus_v2.data.di.appModule
import com.example.lupus_v2.model.manager.GameRules
import com.example.lupus_v2.model.roles.RoleFactory
import com.example.lupus_v2.model.roles.RoleType
import com.example.lupus_v2.ui.commonui.LupusTopAppBar
import com.example.lupus_v2.ui.util.animations.ShinyBouncingAnimation
import org.koin.compose.koinInject

sealed class ChooseRoleUiState {
    data class Random(val roleSelected: List<RoleType> = emptyList()) : ChooseRoleUiState()
    data class Manual(
        val remainingPlayers: Int = 0,
        val roleCount: Map<RoleType, Int> = emptyMap()
    ) : ChooseRoleUiState()
}

@Composable
fun ChooseRoleScreen(
    uiState: ChooseRoleUiState,
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Boolean = { true },
    onSwitchMode: (Boolean) -> Unit = {},
    canNavigateBack: Boolean = true,
    navigateUp: () -> Unit = {},
    onIncrementButtonClick: (RoleType) -> Unit = {},
    onDecrementButtonClick: (RoleType) -> Unit = {},
    onCheckedChange: (RoleType) -> Unit = {},
) {
    var textColorIsError by remember { mutableStateOf(false) }
    val checkTextColorError = {
        textColorIsError =
            !(uiState is ChooseRoleUiState.Manual && uiState.roleCount.values.sum() >= GameRules.MIN_PLAYERS)
    }
    var targetIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            LupusTopAppBar(
                title = stringResource(id = R.string.choose_role),
                canNavigateBack = canNavigateBack,
                navigateUp = navigateUp
            )
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(dimensionResource(id = R.dimen.padding_small))
        ) {
            if (uiState is ChooseRoleUiState.Manual) {
                val playersSizeOk = uiState.remainingPlayers <= 0
                val text = when (playersSizeOk) {
                    true -> stringResource(id = R.string.go_to_order_player)
                    false -> stringResource(
                        id = R.string.players_remaining_to_play,
                        uiState.remainingPlayers
                    )
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium,
                    color = when (textColorIsError && !playersSizeOk) {
                        true -> MaterialTheme.colorScheme.error
                        false -> MaterialTheme.colorScheme.onBackground
                    },
                    modifier = Modifier
                        .padding(vertical = dimensionResource(id = R.dimen.padding_small))
                )
            }
            
            val roles = when (uiState) {
                is ChooseRoleUiState.Manual -> uiState.roleCount.keys.toList()
                is ChooseRoleUiState.Random -> uiState.roleSelected
            }

            val screenHeight = LocalConfiguration.current.screenHeightDp.dp
            LazyVerticalGrid(
                modifier = Modifier.heightIn(min = 100.dp, max = screenHeight / 3),
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
            ) {
                items(roles, key = { it.ordinal }) { roleType ->
                    RoleCard(
                        onClick = { targetIndex = roleType.ordinal },
                        isSelected = true,
                        modifier = Modifier
                            .height(dimensionResource(id = R.dimen.roleCard_height))
                    ) {
                        RoleCardContentOverlapped(
                            roleType = roleType,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_medium))
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.random_roles_count),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = uiState is ChooseRoleUiState.Random,
                    onCheckedChange = { onSwitchMode(it) },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.secondary
                    )
                )
                Spacer(modifier = Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
                    modifier = Modifier.weight(3f)
                ) {
                    ShinyBouncingAnimation(
                        isActive = when (uiState is ChooseRoleUiState.Manual) {
                            true -> uiState.remainingPlayers <= 0
                            else -> true
                        },
                        //modifier = Modifier.fillMaxSize()
                    ) {
                        Button(
                            onClick = { textColorIsError = !onConfirmClick() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                textAlign = TextAlign.Center,
                                text = stringResource(id = R.string.start_game)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_medium))
            )
            val gridState = rememberLazyGridState()
            LaunchedEffect(targetIndex) {
                if (targetIndex != -1) {
                    // Center the item on the screen
                    gridState.animateScrollToItem(targetIndex)
                }
            }

            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
            ) {
                items(RoleType.entries, key = { it.ordinal }) { roleType ->
                    RoleCard(
                        isSelected = when (uiState) {
                            is ChooseRoleUiState.Manual -> uiState.roleCount.containsKey(roleType)// && uiState.roleCount[roleType]!! > 0
                            is ChooseRoleUiState.Random -> uiState.roleSelected.contains(roleType)
                        },
                        modifier = Modifier
                            .size(
                                dimensionResource(id = R.dimen.roleCard_behavior_width),
                                dimensionResource(id = R.dimen.roleCard_behavior_height)
                            )
                    ) {
                        when (uiState) {
                            is ChooseRoleUiState.Manual -> {
                                RoleCardCountBehavior(
                                    onAdd = {
                                        onIncrementButtonClick(roleType)
                                        checkTextColorError()
                                    },
                                    onRemove = {
                                        onDecrementButtonClick(roleType)
                                        checkTextColorError()
                                    },
                                    count = uiState.roleCount[roleType]?.toString()
                                        ?: 0.toString(),
                                    content = {
                                        RoleCardContent(
                                            roleType = roleType,
                                            modifier = Modifier.weight(4f)
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(dimensionResource(id = R.dimen.padding_small))

                                )
                            }

                            is ChooseRoleUiState.Random -> {
                                RoleCardCheckedBehavior(
                                    isChecked = uiState.roleSelected.contains(roleType),
                                    onCheckedChange = { onCheckedChange(roleType) },
                                    content = {
                                        RoleCardContent(
                                            roleType = roleType,
                                            modifier = Modifier.weight(5f)
                                        )
                                    },
                                    modifier = Modifier
                                        .padding(dimensionResource(id = R.dimen.padding_small))
                                        .fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoleCard(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHighest
    }

    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = contentColor,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.large
    ) {
        content()
    }
}

@Composable
fun RoleCardContentOverlapped(
    modifier: Modifier = Modifier,
    roleType: RoleType,
) {
    val roleFactory: RoleFactory = koinInject()

    Box(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = roleFactory.createRole(roleType).image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .size(dimensionResource(id = R.dimen.img_medium))
                .align(Alignment.Center)
        )
        Text(
            textAlign = TextAlign.Center,
            text = roleType.name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    (MaterialTheme.colorScheme.primaryContainer).copy(alpha = 0.9f),
                    MaterialTheme.shapes.medium
                )
                .padding(dimensionResource(id = R.dimen.padding_small))
        )
    }
}

@Composable
fun RoleCardContent(
    modifier: Modifier = Modifier,
    roleType: RoleType,
) {
    val roleFactory: RoleFactory = koinInject()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = roleFactory.createRole(roleType).image),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .weight(2f)
                .clip(MaterialTheme.shapes.medium)
        )
        Text(
            text = roleType.name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(1f)
                .padding(dimensionResource(id = R.dimen.padding_small))
        )
    }
}

@Composable
fun RoleCardCheckedBehavior(
    modifier: Modifier = Modifier,
    isChecked: Boolean = false,
    onCheckedChange: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
        modifier = modifier
    ) {
        content()
        Switch(
            colors = SwitchDefaults.colors(
                //checkedThumbColor = MaterialTheme.colorScheme.secondary,
                checkedTrackColor = MaterialTheme.colorScheme.secondary
            ),
            checked = isChecked,
            onCheckedChange = { onCheckedChange() },
        )
    }
}

@Composable
fun RoleCardCountBehavior(
    modifier: Modifier = Modifier,
    count: String? = null,
    onAdd: () -> Unit = {},
    onRemove: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        content()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_remove_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.secondary)
                )
            }

            if (count != null) {
                Text(
                    textAlign = TextAlign.Center,
                    text = count,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                )
            }

            IconButton(
                onClick = onAdd,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.secondary)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChooseRoleScreenPreviewManual() {
    KoinPreviewApplication(
        modules = { listOf(appModule) }
    ) {
        var i = 1
        ChooseRoleScreen(
            uiState = ChooseRoleUiState.Manual(
                roleCount = RoleType.entries.associateWith { i++ }
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseRoleScreenPreviewRandom() {
    KoinPreviewApplication(
        modules = { listOf(appModule) }
    ) {
        ChooseRoleScreen(
            uiState = ChooseRoleUiState.Random(
                roleSelected = listOf(
                    RoleType.Assassino,
                    RoleType.Cittadino,
                    RoleType.Cupido,
                    RoleType.Seduttrice,
                    RoleType.Medium,
                    RoleType.Veggente
                )
            )
        )
    }
}