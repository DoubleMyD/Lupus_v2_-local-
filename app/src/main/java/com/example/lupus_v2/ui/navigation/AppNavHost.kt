package com.example.lupus_v2.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lupus_v2.ui.navigation.setup.NavigationAction
import com.example.lupus_v2.ui.navigation.setup.Navigator
import com.example.lupus_v2.ui.navigation.setup.ObserveAsEvents
import com.example.lupus_v2.ui.screens.home.HomeScreen
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

sealed interface Destination {
    @Serializable data object Home : Destination
}


fun NavGraphBuilder.addRelatedScreens(
    navController: NavHostController,
    screenHandler: NavGraphBuilder.(NavHostController) -> Unit
) {
    // This will invoke the screen handler to add the tied screens
    screenHandler(navController)
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
){
    val navigator = koinInject<Navigator>()

    ObserveAsEvents(flow = navigator.navigationActions) { action ->
        when(action) {
            is NavigationAction.Navigate -> navController.navigate(
                action.destination
            ) {
                action.navOptions(this)
            }
            NavigationAction.NavigateUp -> navController.navigateUp()
        }
    }

    NavHost(
        navController = navController,
        startDestination = navigator.startDestination,
        modifier = modifier
    ){
        composable<Destination.Home> {
            HomeScreen(
                navigateToSettings = { navController.navigate(Destination.Home) },
                navigateToGame = { navController.navigate(GameDestination.ChoosePlayers) },
                navigateToLoadGame = { navController.navigate(Destination.Home) },
                navigateToPlayersArchive = { navController.navigate(PlayerDestination.Players) }
            )
        }

        addRelatedScreens(
            navController = navController,
            screenHandler = { playerNavigation(navController) }
        )

        addRelatedScreens(
            navController = navController,
            screenHandler = { playersListNavigation(navController) }
        )

        addRelatedScreens(
            navController = navController,
            screenHandler = { gameNavigation(navController) }
        )
    }
}