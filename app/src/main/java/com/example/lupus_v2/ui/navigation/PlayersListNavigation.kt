package com.example.lupus_v2.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.lupus_v2.ui.screens.list.list_archive.ListsArchiveScreen
import com.example.lupus_v2.ui.screens.list.list_archive.ListsArchiveViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

sealed interface PlayersListDestination: Destination {
    @Serializable
    data object Lists: PlayersListDestination

    @Serializable
    data class ListEdit (
        val listId: Int,
    ) : PlayersListDestination

}

fun NavGraphBuilder.playersListNavigation(
    navController: NavHostController,
) {
    composable<PlayersListDestination.Lists> {
        val playersListViewModel: ListsArchiveViewModel = koinInject()
        val uiState by playersListViewModel.databaseLists.collectAsState()

        ListsArchiveScreen(
            uiState = uiState,
            onListClick = { list -> navController.navigate(PlayersListDestination.ListEdit(list.id)) },
            addNewList = { name -> playersListViewModel.addNewList(name)} ,
            modifier = Modifier.fillMaxSize(),
            canNavigateBack = true,
            navigateBack = { navController.navigateUp() }
        )
    }

    composable<PlayersListDestination.ListEdit> {

    }
}