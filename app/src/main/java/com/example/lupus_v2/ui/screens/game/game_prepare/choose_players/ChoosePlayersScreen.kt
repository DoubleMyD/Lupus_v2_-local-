package com.example.lupus_v2.ui.screens.game.game_prepare.choose_players

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lupus_v2.R
import com.example.lupus_v2.data.fake.FakePlayersRepository
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.ui.commonui.LupusTopAppBar
import com.example.lupus_v2.ui.commonui.PlayerImage
import kotlinx.coroutines.delay

data class ChoosePlayersUiState(
    val minimumPlayers: Int = 6,
    val playersSelected: List<PlayerDetails> = emptyList(),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoosePlayersScreen(
    players: List<PlayerDetails>,
    uiState: ChoosePlayersUiState,
    modifier: Modifier = Modifier,
    onPlayerClick: (PlayerDetails) -> Unit = {},
    onConfirmPlayersClick: () -> Boolean = { true },
    canNavigateBack: Boolean = true,
    navigateUp: () -> Unit = {},
) {
    var expanded by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") } // Holds the search query
    val filteredPlayers = if (searchQuery.isNotEmpty()) {
        players.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        } // Filters players based on query
    } else {
        players
    }

    var textColorIsError by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            LupusTopAppBar(
                title = stringResource(id = R.string.choose_players),
                canNavigateBack = canNavigateBack,
                navigateUp = navigateUp
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (!onConfirmPlayersClick()) {
                        textColorIsError = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text(text = stringResource(id = R.string.confirm_players))
                Icon(imageVector = Icons.Filled.Done, contentDescription = null)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            SearchBar(
                modifier = Modifier,
                inputField = {
                    SearchBarDefaults.InputField(
                        query = searchQuery,
                        onQueryChange = { query -> searchQuery = query },
                        expanded = expanded,
                        onExpandedChange = { expanded = true },
                        onSearch = { expanded = true },
                        placeholder = { Text(text = stringResource(id = R.string.search_players)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null
                            )
                        },
                        colors = SearchBarDefaults.inputFieldColors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            selectionColors = LocalTextSelectionColors.current,
                            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                            focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface
                        ),
                    )
                },
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background,
                    dividerColor = MaterialTheme.colorScheme.onBackground,
                ),
                expanded = expanded,
                onExpandedChange = { expanded = true }
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(dimensionResource(id = R.dimen.padding_small))
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.players_necessary_to_play,
                            uiState.playersSelected.size,
                            uiState.minimumPlayers
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = when (textColorIsError) {
                            true -> MaterialTheme.colorScheme.error
                            false -> MaterialTheme.colorScheme.onBackground
                        },
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
                    )
                    Players(
                        players = filteredPlayers,
                        selectedPlayers = uiState.playersSelected,
                        onClick = { player ->
                            onPlayerClick(player)
                            if (uiState.playersSelected.size >= uiState.minimumPlayers-1) {
                                textColorIsError = false
                            }
                        },
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_small))
                    )
                }
            }
        }
    }
}


@Composable
fun Players(
    players: List<PlayerDetails>,
    modifier: Modifier = Modifier,
    selectedPlayers: List<PlayerDetails> = emptyList(),
    onClick: (PlayerDetails) -> Unit = {}
) {
    val visibilityStates = remember(players) { players.map { mutableStateOf(false) } }
    val visibilityDelay = remember { 100L }

    // Launch effect to update visibility states sequentially
    LaunchedEffect(players) {
        players.forEachIndexed { index, _ ->
            delay(visibilityDelay) // Delay before making the next item visible
            visibilityStates[index].value = true
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
    ) {
        itemsIndexed(players, key = { index, player -> player.id }) { index, player ->
            val isVisible = visibilityStates.getOrElse(index) { mutableStateOf(false) }.value
            val color = when (selectedPlayers.contains(player)) {
                true -> MaterialTheme.colorScheme.primaryContainer
                false -> MaterialTheme.colorScheme.surfaceContainer
            }

            AnimatedVisibility(
                visible = isVisible,
            ) {
                OutlinedCard(
                    onClick = { onClick(player) },
                    colors = CardDefaults.cardColors(containerColor = color),
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_very_small))
                        .size(
                            dimensionResource(id = R.dimen.img_big),
                            dimensionResource(id = R.dimen.img_big) + 16.dp
                        ),
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dimensionResource(id = R.dimen.padding_very_small))
                    ) {
                        PlayerImage(
                            imageSource = player.imageSource,
                            borderColor = if (selectedPlayers.contains(player)) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else {
                                Color.Transparent
                            },
                            modifier = Modifier
                                .weight(7f)
                        )
                        Text(
                            textAlign = TextAlign.Center,
                            text = player.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1.5f)
                        )
                    }
                }
            }
        }
    }
}

//
//@Composable
//fun PlayerSummary(
//    playersSelected: List<PlayerDetails>,
//    modifier: Modifier = Modifier
//) {
//    Column(
//        verticalArrangement = Arrangement.Top,
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = modifier,
//    ) {
//        Text(
//            textAlign = TextAlign.Center,
//            text = stringResource(id = R.string.selected_players),
//            style = MaterialTheme.typography.titleSmall,
//            color = MaterialTheme.colorScheme.onBackground,
//            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_very_small))
//        )
//        LazyColumn(
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            items(playersSelected, key = { it.id }) { player ->
//                PlayerImage(
//                    imageSource = player.imageSource,
//                    modifier = Modifier
//                        .size(dimensionResource(id = R.dimen.img_medium))
//                )
//            }
//        }
//    }
//}


@Preview(showBackground = true)
@Composable
fun ChoosePlayersScreenPreview() {
    ChoosePlayersScreen(
        players = FakePlayersRepository.playerDetails,
        uiState = ChoosePlayersUiState(
            playersSelected = listOf(
                FakePlayersRepository.playerDetails[0],
                FakePlayersRepository.playerDetails[1]
            )
        ),
    )
}
