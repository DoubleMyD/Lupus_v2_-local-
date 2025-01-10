package com.example.lupus_v2.ui.navigation.setup

import androidx.navigation.NavOptionsBuilder
import com.example.lupus_v2.ui.navigation.Destination

sealed interface NavigationAction {

    data class Navigate(
        val destination: Destination,
        val navOptions: NavOptionsBuilder.() -> Unit = {}
    ): NavigationAction

    data object NavigateUp: NavigationAction
}