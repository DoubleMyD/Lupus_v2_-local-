package com.example.lupus_v2.ui.screens.list.list_edit

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.lupus_v2.R
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.ui.commonui.FAB_Column_Search_Add
import com.example.lupus_v2.ui.commonui.LupusTopAppBar
//import com.example.lupus_v2.ui.screens.player.player_archive.PlayersList
import com.example.lupus_v2.ui.util.InputDialog

@Composable
fun ListEditScreen(
    listId: Int,
    players: List<PlayerDetails>,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    navigateBack: () -> Unit = {},
){
    var showSearchField by remember { mutableStateOf(false) }
    var playerNameToScroll by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            LupusTopAppBar(
                title = stringResource(id = R.string.edit_list),
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
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
//        PlayersList(
//            modifier = Modifier.padding(innerPadding),
//            players = players,
//            onPlayerClick = {player -> Log.d("ListEditScreen", "Player clicked: ${player.name}")},
//            scrollToPlayerName = playerNameToScroll
//        )

            if (showSearchField) {
                InputDialog(
                    title = stringResource(id = R.string.search_player),
                    placeholder = stringResource(id = R.string.search_player_placeholder),
                    onDismiss = { showSearchField = false },
                    onConfirm = { playerName ->
                        playerNameToScroll = playerName.trim()
                        showSearchField = false
                    }
                )
            }
        }
    }
}