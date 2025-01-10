package com.example.lupus_v2.ui.screens.player.player_archive

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.lupus_v2.R
import com.example.lupus_v2.data.database.entity.Player
import com.example.lupus_v2.data.fake.FakePlayersRepository
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.getPainter
import com.example.lupus_v2.model.toPlayer
import com.example.lupus_v2.ui.commonui.CancelAndConfirmButtons
import com.example.lupus_v2.ui.commonui.FAB_Column_Search_Add
import com.example.lupus_v2.ui.commonui.LupusTopAppBar

data class PlayersArchiveUiState(
    val players: List<PlayerDetails> = emptyList(),
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PlayersArchiveScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    navigateBack: () -> Unit = {},
    players: List<PlayerDetails>,
    onPlayerClick: (PlayerDetails) -> Unit = {},
    onAddButtonClick: () -> Unit = {},
    onDeletePlayer: (Player) -> Unit = {}
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") } // Holds the search query
    val filteredPlayers = if (searchQuery.isNotEmpty()) {
        players.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }
    } else {
        players
    }

    var showDeleteOption by remember { mutableStateOf(false) }
    val toggleDeleteOption = { showDeleteOption = !showDeleteOption }

    var showDialogConfirm by remember { mutableStateOf(false) }
    var playerToDelete by remember { mutableStateOf<Player?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            LupusTopAppBar(
                title = stringResource(id = R.string.players_archive),
                canNavigateBack = canNavigateBack,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            FAB_Column_Search_Add(
                onAddButtonClick = onAddButtonClick
            )
        }
    ) { innerPadding ->
        Box(
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
                LazyColumn(
                    modifier = modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .combinedClickable(
                            onClick = { },
                            onLongClick = { toggleDeleteOption() }
                        )
                ) {
                    items(filteredPlayers, key = { player -> player.id }) { player ->
                        PlayerInfoContent(
                            modifier = Modifier
                                .height(dimensionResource(id = R.dimen.playerCard_info_height))
                                .background(MaterialTheme.colorScheme.background)
                                .combinedClickable(
                                    onClick = { if(!showDeleteOption) onPlayerClick(player) },
                                    onLongClick = { toggleDeleteOption() }
                                )
                                .padding(dimensionResource(id = R.dimen.padding_small)),
                            player = player,
                            onDeletePlayer = if (showDeleteOption) {
                                {
                                    showDialogConfirm = true; playerToDelete =
                                    player.toPlayer(context)
                                }
                            } else {
                                null
                            }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            if (showDialogConfirm) {
                ConfirmDeleteAlert(
                    onDismissRequest = { showDialogConfirm = false; playerToDelete = null },
                    onConfirmClick = {
                        showDialogConfirm = false
                        onDeletePlayer(playerToDelete!!)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmDeleteAlert(
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = { onDismissRequest() },
        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.padding( dimensionResource(id = R.dimen.padding_medium) )
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.delete_player_confirm),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
            CancelAndConfirmButtons(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                onConfirmClick = { onConfirmClick() },
                onCancelClick = { onDismissRequest() }
            )
        }
    }
}

@Composable
fun PlayerInfoContent(
    modifier: Modifier = Modifier,
    onDeletePlayer: (() -> Unit)? = null,
    player: PlayerDetails,
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(dimensionResource(id = R.dimen.padding_small))
    ) {
        if (onDeletePlayer != null) {
            IconButton(onClick = onDeletePlayer) {
                Icon(
                    painter = painterResource(R.drawable.baseline_not_interested_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                )
            }
        }
        Image(
            painter = player.imageSource.getPainter(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(dimensionResource(id = R.dimen.playerCard_info_height))
                .clip(MaterialTheme.shapes.large)
        )
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
        Text(
            text = player.name,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PlayersArchiveScreenPreview() {
    PlayersArchiveScreen(
        modifier = Modifier.fillMaxSize(),
        canNavigateBack = false,
        players = FakePlayersRepository.playerDetails
    )
}
