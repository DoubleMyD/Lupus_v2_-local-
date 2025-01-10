package com.example.lupus_v2.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.lupus_v2.ui.screens.player.player_edit.PlayerEditScreen
import com.example.lupus_v2.ui.screens.player.player_edit.PlayerEditViewModel
import com.example.lupus_v2.ui.screens.player.player_new.NewPlayerViewModel
import com.example.lupus_v2.ui.screens.player.player_new.PlayerNewScreen
import com.example.lupus_v2.ui.screens.player.player_archive.PlayersArchiveScreen
import com.example.lupus_v2.ui.screens.player.player_archive.PlayersArchiveViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

sealed interface PlayerDestination : Destination {
    @Serializable
    data object Players : PlayerDestination

    @Serializable
    data class PlayerEdit(
        val playerId: Int
    ) : PlayerDestination

    @Serializable
    data object PlayerNew : PlayerDestination
}

fun NavGraphBuilder.playerNavigation(
    navController: NavHostController
) {
    composable<PlayerDestination.Players> {
        val playersArchiveViewModel: PlayersArchiveViewModel = koinInject()
        val databasePlayers by playersArchiveViewModel.databasePlayers.collectAsState()

        PlayersArchiveScreen(
            onPlayerClick = { player -> navController.navigate(PlayerDestination.PlayerEdit(player.id)) },
            players = databasePlayers.players,
            canNavigateBack = true,
            navigateBack = { navController.navigateUp() },
            onAddButtonClick = { navController.navigate(PlayerDestination.PlayerNew) },
            onDeletePlayer = { player -> playersArchiveViewModel.deletePlayer(player) },
            modifier = Modifier.fillMaxSize()
        )
    }

    composable<PlayerDestination.PlayerNew> {
        val newPlayerViewModel: NewPlayerViewModel = koinInject()
        PlayerNewScreen(
            navigateBack = { navController.navigateUp() },
            modifier = Modifier.fillMaxSize(),
            onConfirmClick = { context, playerName, imageUri ->
                newPlayerViewModel.savePlayer(
                    context,
                    playerName,
                    imageUri
                )
            }
        )
    }

    composable<PlayerDestination.PlayerEdit> {
        val args = it.toRoute<PlayerDestination.PlayerEdit>()
        val playerEditViewModel: PlayerEditViewModel = koinInject()
        val uiState by playerEditViewModel.uiState.collectAsState()
        // Trigger loading of the player details
        LaunchedEffect(args.playerId) {
            playerEditViewModel.loadPlayer(args.playerId)
        }

        val coroutineScope = rememberCoroutineScope()
        PlayerEditScreen(
            uiState = uiState,
            updatePlayer = { id, name, bitmap, context ->
                coroutineScope.launch {
                    playerEditViewModel.updatePlayer(id, name, bitmap, context)
                }
            },
            navigateBack = { navController.navigateUp() }
        )
    }
}