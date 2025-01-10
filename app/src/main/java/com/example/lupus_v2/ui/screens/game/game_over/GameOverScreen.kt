package com.example.lupus_v2.ui.screens.game.game_over

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lupus_v2.R
import com.example.lupus_v2.data.di.KoinPreviewApplication
import com.example.lupus_v2.data.di.appModule
import com.example.lupus_v2.data.fake.FakePlayersRepository
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.roles.RoleType
import com.example.lupus_v2.ui.commonui.LupusTopAppBar
import com.example.lupus_v2.ui.commonui.PlayerImage

@Composable
fun GameOverScreen(
    winnerRole: RoleType,
    winnerPlayers: List<PlayerDetails>,
    goToHome: () -> Unit = {},
    canNavigateBack: Boolean = true,
    navigateUp: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            LupusTopAppBar(
                title = stringResource(id = R.string.game_over, winnerRole.toString()),
                canNavigateBack = canNavigateBack,
                navigateUp = navigateUp
            )
        }
    ) { innerPadding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            Text(
                text = "You won!",
                style = MaterialTheme.typography.headlineMedium
            )
            LazyRow (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = modifier.fillMaxWidth()
            ){
                items(winnerPlayers, key = { it.id }) { player ->
                    PlayerImage(
                        imageSource = player.imageSource,
                        modifier = Modifier.padding(8.dp).size(dimensionResource(id = R.dimen.img_medium))
                    )
                }
            }

            Button(
                onClick = goToHome,
                modifier = Modifier.padding(16.dp)
            ){
                Text(text = "Go to home")
            }

        }

    }
}


@Preview(showBackground = true)
@Composable
fun GameOverScreenPreview() {
    KoinPreviewApplication(
        modules = { listOf(appModule) }
    ) {
        GameOverScreen(
            winnerRole = RoleType.Assassino,
            winnerPlayers = FakePlayersRepository.playerDetails.subList(0, 3)
        )
    }
}