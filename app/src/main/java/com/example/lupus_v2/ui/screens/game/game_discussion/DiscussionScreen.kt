package com.example.lupus_v2.ui.screens.game.game_discussion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.example.lupus_v2.R
import com.example.lupus_v2.data.di.KoinPreviewApplication
import com.example.lupus_v2.data.di.appModule
import com.example.lupus_v2.data.fake.FakePlayersRepository
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.ui.commonui.DeadOverlay
import com.example.lupus_v2.ui.commonui.LupusTopAppBar
import com.example.lupus_v2.ui.commonui.PlayerImage
import com.example.lupus_v2.ui.commonui.Village
import com.example.lupus_v2.ui.util.TimerSection

@Composable
fun DiscussionScreen(
    players: List<PlayerDetails>,
    onTimerFinished: () -> Unit,
    timerDuration: Int = 120,
    canNavigateBack: Boolean = true,
    navigateUp: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            LupusTopAppBar(
                title = stringResource(id = R.string.discussion),
                canNavigateBack = canNavigateBack,
                navigateUp = navigateUp
            )
        },
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Village(
                players = players,
                swipeEnabled = false,
                modifier = Modifier.fillMaxSize()
            ) { player ->
                Card(
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    DeadOverlay (!player.alive){
                        PlayerImage(
                            imageSource = player.imageSource,
                            padding = 0,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            TimerSection(
                skipTimerDialogText = stringResource(id = R.string.sure_to_skip_timer),
                onTimerFinished = onTimerFinished,
                circleRadius = dimensionResource(id = R.dimen.img_big).value.toInt(),
                timerDuration = timerDuration,
                showLeftTime = true,
                enableSkip = true,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))

            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiscussionScreenPreview() {
    KoinPreviewApplication(
        modules = { listOf(appModule) }
    ) {
        DiscussionScreen(
            players = FakePlayersRepository.playerDetails.subList(0, 8),
            onTimerFinished = {}
        )
    }
}