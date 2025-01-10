package com.example.lupus_v2.ui.screens.game.game_citizien_vote

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.util.fastFilter
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
import com.example.lupus_v2.ui.util.animations.TriggerableBouncyAnimation


data class CitizenVoteUiState(
    val playersThatAlreadyVoted: List<PlayerDetails> = emptyList(),
    val voter: PlayerDetails = FakePlayersRepository.errorPlayer,
    val voteCount: Map<Int, Int> = emptyMap(), //id of player to vote count
)

@Composable
fun CitizenVoteScreen(
    uiState: CitizenVoteUiState,
    players: List<PlayerDetails>,
    votePlayer: (PlayerDetails) -> Unit,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    navigateUp: () -> Unit = {}
) {
    var bouncingPlayer by remember { mutableStateOf<List<PlayerDetails>>( emptyList() ) } //if null, no animation is running

    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            LupusTopAppBar(
                title = stringResource(id = R.string.choose_players),
                canNavigateBack = canNavigateBack,
                navigateUp = navigateUp
            )
        }
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
                onClicked = { player ->
                    if (player.alive) {
                        votePlayer(player)
                    } else {
                        bouncingPlayer += player
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) { player ->

                val border = when {
                    player.alive.not() -> BorderStroke(
                        dimensionResource(id = R.dimen.border_small),
                        Color.Transparent
                    )

                    uiState.voter.id == player.id -> BorderStroke(
                        dimensionResource(id = R.dimen.border_medium),
                        MaterialTheme.colorScheme.primary
                    )

                    uiState.playersThatAlreadyVoted.contains(player) -> BorderStroke(
                        dimensionResource(id = R.dimen.border_small),
                        MaterialTheme.colorScheme.tertiary
                    )

                    else -> BorderStroke(
                        dimensionResource(id = R.dimen.border_small),
                        Color.Transparent
                    )
                }

                TriggerableBouncyAnimation(
                    bounceDuration = 100,
                    totalDuration = 1000,
                    isBouncing = bouncingPlayer.contains(player),
                    onAnimationEnd = { bouncingPlayer = bouncingPlayer.filter { it != player } },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Card(
                        border = border,
                        shape = when (player == uiState.voter) {
                            true -> MaterialTheme.shapes.extraLarge
                            false -> MaterialTheme.shapes.medium
                        },
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        DeadOverlay(!player.alive) {
                            Box(
                                contentAlignment = Alignment.TopStart,
                            ) {
                                PlayerImage(
                                    imageSource = player.imageSource,
                                    padding = 0,
                                    modifier = Modifier.fillMaxSize(),
                                )
                                //Text( player.name, modifier.background(MaterialTheme.colorScheme.background))
                                if (player.alive) {
                                    Text(
                                        textAlign = TextAlign.Center,
                                        text = (uiState.voteCount[player.id] ?: 0).toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier
                                            .width(dimensionResource(id = R.dimen.img_small))
                                            .padding(dimensionResource(id = R.dimen.padding_very_small))
                                            .background(
                                                color = MaterialTheme.colorScheme.surface,
                                                shape = MaterialTheme.shapes.small
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
            TimerSection(
                timerKey = uiState.voter,
                timerDuration = 20,
                skipTimerDialogText = stringResource(id = R.string.skip_vote),
                enableSkip = true,
                onTimerFinished = { votePlayer(players.filter { it.alive }.random()) },
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.img_big))
//                    .background(
//                        MaterialTheme.colorScheme.surfaceVariant,
//                        shape = MaterialTheme.shapes.extraLarge
//                    )
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CitizenVoteScreePreview() {
    KoinPreviewApplication(
        modules = { listOf(appModule) }
    ) {
        CitizenVoteScreen(
            uiState = CitizenVoteUiState(
                voter = FakePlayersRepository.playerDetails.first(),
                //runNotValidAnimation = true
            ),
            players = FakePlayersRepository.playerDetails.subList(0, 8)
                .map { if (it.id % 2 == 0) it.copy(alive = false) else it },
            votePlayer = {},
        )
    }
}