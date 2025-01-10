package com.example.lupus_v2.ui.screens.list.list_archive

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.lupus_v2.R
import com.example.lupus_v2.data.database.entity.PlayersList
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.getPainter
import com.example.lupus_v2.ui.commonui.FAB_Column_Search_Add
import com.example.lupus_v2.ui.commonui.LupusTopAppBar
import com.example.lupus_v2.ui.util.InputDialog

sealed class ListsArchiveUiState {
    data object Loading : ListsArchiveUiState()
    data class Success(val lists: Map<PlayersList, List<PlayerDetails>>) : ListsArchiveUiState()
    data object Error : ListsArchiveUiState()
}


@Composable
fun ListsArchiveScreen(
    uiState: ListsArchiveUiState,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    navigateBack: () -> Unit = {},
    onListClick: (PlayersList) -> Unit = {},
    addNewList: (String) -> Unit = {}
) {
    var showAddList by remember { mutableStateOf(false) }
    var showSearchField by remember { mutableStateOf(false) }
    var playerNameToScroll by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            LupusTopAppBar(
                title = stringResource(id = R.string.lists_archive),
                canNavigateBack = canNavigateBack,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            FAB_Column_Search_Add(
                onSearchButtonClick = { showSearchField = !showSearchField },
                onAddButtonClick = {}
            )
        }
    ) { innerPadding ->
        when (uiState) {
            is ListsArchiveUiState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    CircularProgressIndicator()
                }
            }

            is ListsArchiveUiState.Error -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Text(text = stringResource(id = R.string.error_loading_lists))
                }
            }

            is ListsArchiveUiState.Success -> {
                PlayersList(
                    modifier = Modifier.padding(innerPadding),
                    lists = uiState.lists,
                    onListClick = onListClick,
                    scrollToPlayerName = playerNameToScroll
                )

                if (showSearchField) {
                    InputDialog(
                        title = stringResource(id = R.string.search_player),
                        placeholder = stringResource(id = R.string.search_player_placeholder),
                        onDismiss = { showSearchField = false },
                        onConfirm = { listName ->
                            playerNameToScroll = listName.trim()
                            showSearchField = false
                        }
                    )
                }

                if(showAddList){
                    InputDialog(
                        title = stringResource(id = R.string.add_new_list),
                        onDismiss = { showAddList = false },
                        onConfirm = { if(it.isNotBlank()) addNewList(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun PlayersList(
    modifier: Modifier = Modifier,
    lists: Map<PlayersList, List<PlayerDetails>>,
    onListClick: (PlayersList) -> Unit = {},
    scrollToPlayerName: String? = null
) {
    val listState = rememberLazyListState()

    // Determine scroll target based on player name
    LaunchedEffect(scrollToPlayerName) {
        scrollToPlayerName?.let { name ->
            lists.keys.indexOfFirst { it.name == name }.takeIf { it >= 0 }?.let { targetIndex ->
                listState.animateScrollToItem(targetIndex)
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        items(lists.toList(), key = { it.first.id }) { (list, players) ->
            ListCard(
                list = list,
                players = players,
                onListClick = onListClick
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun ListCard(
    list: PlayersList,
    players: List<PlayerDetails>,
    onListClick: (PlayersList) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onListClick(list) }
            .padding(dimensionResource(id = R.dimen.padding_small))
    ) {
        Text(text = list.name)//style = MaterialTheme.typography.h6)
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            players.forEach { player ->
                Image(
                    painter = player.imageSource.getPainter(),
                    contentDescription = player.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(dimensionResource(id = R.dimen.img_medium))
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListsArchiveScreenPreviewLoading(){
    ListsArchiveScreen(uiState = ListsArchiveUiState.Loading)
}

@Preview(showBackground = true)
@Composable
fun ListsArchiveScreenPreviewError(){
    ListsArchiveScreen(uiState = ListsArchiveUiState.Error)
}

@Preview(showBackground = true)
@Composable
fun ListsArchiveScreenPreviewSuccess(){
    ListsArchiveScreen(uiState = ListsArchiveUiState.Success(emptyMap()))
}