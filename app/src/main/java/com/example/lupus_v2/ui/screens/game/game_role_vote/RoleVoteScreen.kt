package com.example.lupus_v2.ui.screens.game.game_role_vote

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lupus_v2.R
import com.example.lupus_v2.data.di.KoinPreviewApplication
import com.example.lupus_v2.data.di.appModule
import com.example.lupus_v2.data.fake.FakePlayersRepository
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.PlayerImageSource
import com.example.lupus_v2.model.roles.RoleType
import com.example.lupus_v2.ui.commonui.DeadOverlay
import com.example.lupus_v2.ui.commonui.LupusTopAppBar
import com.example.lupus_v2.ui.commonui.PlayerImage
import com.example.lupus_v2.ui.util.TimerSection
import com.example.lupus_v2.ui.util.animations.TriggerableBouncyAnimation

data class RoleVoteUiState(
    val playersToVote: List<PlayerDetails> = emptyList(),
    val voter: PlayerDetails = FakePlayersRepository.errorPlayer,
    val votedPlayers: List<PlayerDetails> = emptyList(),
    val playersNotValidBouncing: List<PlayerDetails> = emptyList(), //used to inform the user that the voted player is not valid
)

@Composable
fun RoleVoteScreen(
    uiState: RoleVoteUiState,
    modifier: Modifier = Modifier,
    canVote: Boolean = true,
    onTimerFinished: () -> Unit = {},
    onVoteClick: (PlayerDetails) -> Unit = {},
    onConfirmVoteClick: (List<PlayerDetails>) -> Boolean = { true },
    clearPlayerToBounce: (player: PlayerDetails) -> Unit = {},
    canNavigateBack: Boolean = false,
    navigateUp: () -> Unit = {},
) {
    var showRoleInfo by remember { mutableStateOf(false) }

    if (showRoleInfo) {
        RoleAlertExplanation(
            imageRes = uiState.voter.role.image,
            content = uiState.voter.role.roleDescription,
            title = uiState.voter.role.roleType.toString(),
            onDismissRequest = { showRoleInfo = false }
        )
    }

    Scaffold(
        modifier = modifier,//.background(MaterialTheme.colorScheme.surface),
        topBar = {
            LupusTopAppBar(
                title = uiState.voter.name,
                canNavigateBack = canNavigateBack,
                navigateUp = navigateUp,
                actions = {
                    IconButton(onClick = { showRoleInfo = !showRoleInfo }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_question_mark_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer // Icon tint
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface) // Screen background
        ) {
            VoterInfo(
                player = uiState.voter,
                modifier = Modifier
                    .height((dimensionResource(id = R.dimen.img_big).value * 1.5).dp)
                    .fillMaxWidth()
            )
            HorizontalDivider(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant) // Divider color
            )
            VoteSummary(
                voter = uiState.voter,
                votePlayers = uiState.votedPlayers,
                onConfirmClick = { onConfirmVoteClick(uiState.votedPlayers) },
                playerToVoteCount = uiState.voter.role.getVoteStrategyCount(),
                modifier = Modifier
                    .height(dimensionResource(id = R.dimen.img_medium) + 16.dp)
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.padding_small))
            )
            HorizontalDivider(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant) // Divider color
            )
            if (canVote) {
                VoteSection(
                    voter = uiState.voter,
                    votedPlayer = uiState.votedPlayers,
                    playersToVote = uiState.playersToVote,
                    onPlayerClick = onVoteClick,
                    playersToBounce = uiState.playersNotValidBouncing,
                    clearPlayerToBounce = clearPlayerToBounce,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant) // Section background
                )
            } else {
                TimerSection(
                    onTimerFinished = onTimerFinished,
                    circleRadius = 128,
                    timerDuration = 30,
                    showLeftTime = true,
                    enableSkip = true,
                    skipTimerDialogText = stringResource(id = R.string.sure_to_skip_timer),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun VoteSummary(
    voter: PlayerDetails,
    playerToVoteCount: Int,
    votePlayers: List<PlayerDetails>,
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Boolean = { true },
) {
    var isBouncing by remember { mutableStateOf(false) }
    val getDuration = { (150..250).random() }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.weight(0.7f)
        ) {
            if (votePlayers.isNotEmpty()) {
                for (player in votePlayers) {
                    PlayerImage(
                        imageSource = player.imageSource,
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.img_medium))
                    )
                }
                if (votePlayers.size < playerToVoteCount) {
                    TriggerableBouncyAnimation(
                        bounceDuration = getDuration(),
                        isBouncing = isBouncing,
                        onAnimationEnd = { isBouncing = false },
                        //modifier = Modifier.weight(0.7f)
                    ) {
                        PlayerImage(
                            imageSource = PlayerImageSource.DrawableSource(R.drawable.baseline_person_4_24),
                            modifier = Modifier
                                .size(dimensionResource(id = R.dimen.img_medium))
                        )
                    }
                }
            } else {
                for (i in 1..playerToVoteCount) {
                    TriggerableBouncyAnimation(
                        bounceDuration = getDuration(),
                        isBouncing = isBouncing,
                        onAnimationEnd = { isBouncing = false },
                        //modifier = Modifier.weight(0.7f)
                    ) {
                        PlayerImage(
                            imageSource = PlayerImageSource.DrawableSource(R.drawable.baseline_person_4_24),
                            modifier = Modifier
                                .size(dimensionResource(id = R.dimen.img_medium))
                        )
                    }
                }
            }
        }

        TriggerableBouncyAnimation(
            bounceDuration = 300,
            totalDuration = 1000,
            isBouncing = isBouncing,
            onAnimationEnd = { isBouncing = false },
            modifier = Modifier.weight(0.3f)
        ) {
            Button(
                enabled = votePlayers.size == voter.role.getVoteStrategyCount(),//votePlayers.isNotEmpty() ,
                onClick = {
                    val isVoteConfirmed = onConfirmClick()
                    if (!isVoteConfirmed) isBouncing = true
                },
                //modifier = Modifier.weight(0.3f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Button background
                    contentColor = MaterialTheme.colorScheme.onPrimary // Text color
                )
            ) {
                Text(text = stringResource(id = R.string.vote))
            }
        }
    }
}

@Composable
fun VoteSection(
    voter: PlayerDetails,
    votedPlayer: List<PlayerDetails>,
    playersToVote: List<PlayerDetails>,
    playersToBounce: List<PlayerDetails> = emptyList(),
    clearPlayerToBounce: (player: PlayerDetails) -> Unit = {},
    modifier: Modifier = Modifier,
    onPlayerClick: (PlayerDetails) -> Unit = {},
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier
    ) {
        items(playersToVote, key = { it.id }) { player ->
            val color = when {
                votedPlayer.contains(player) -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
            TriggerableBouncyAnimation(
                bounceDuration = 300,
                totalDuration = 1000,
                isBouncing = playersToBounce.contains(player),
                onAnimationEnd = { clearPlayerToBounce(player) },
                modifier = Modifier
            ) {
            OutlinedCard(
                enabled = player.alive,
                colors = CardDefaults.cardColors(
                    containerColor = color,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                onClick = { onPlayerClick(player) },
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_very_small))
                    .size(dimensionResource(id = R.dimen.img_big) + 16.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (player.alive) {
                        Text(
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            text = player.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                //.weight(1f)
                                .fillMaxWidth()
                                .padding(dimensionResource(id = R.dimen.padding_very_small))

                        )
                    }
                    Box {
                        PlayerInfo(
                            player = player,
                            //modifier = Modifier.weight(3f)
                        )
                        if (player.role.roleType == voter.role.roleType && player.role.roleType != RoleType.Cittadino) {
                            Box(
                                modifier = Modifier
                                    .size(dimensionResource(id = R.dimen.img_small))
                                    .align(Alignment.BottomEnd)
                                    .background(
                                        MaterialTheme.colorScheme.secondaryContainer.copy(1f),
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(dimensionResource(id = R.dimen.padding_small))

                            ) {
                                Image(
                                    painter = painterResource(voter.role.image),
                                    contentScale = ContentScale.Crop,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(dimensionResource(id = R.dimen.img_small))
                                        .clip(MaterialTheme.shapes.small)
                                        .align(Alignment.BottomEnd)
                                )
                            }
                        }
                    }
                }
            }
            }
        }
    }
}

@Composable
fun VoterInfo(
    player: PlayerDetails,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        PlayerInfo(
            player = player,
            modifier = Modifier.weight(1f)
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
        ) {
            Image(
                painter = painterResource(id = player.role.image),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(id = R.dimen.img_medium))
            )
            Text(
                text = player.role.roleType.toString(),
                color = MaterialTheme.colorScheme.onSurface, // Text color
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleAlertExplanation(
    @DrawableRes imageRes : Int,
    title: String,
    content: String,
    onDismissRequest: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = { onDismissRequest() },
        modifier = Modifier.size(300.dp)
    ) {
        Card(
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(dimensionResource(id = R.dimen.padding_medium))
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f)//.background(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)))
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(id = R.dimen.img_medium))
                    )
                }
                //HorizontalDivider(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxSize(0.7f)//.padding( vertical = dimensionResource(id = R.dimen.padding_medium))
                )
            }
        }
    }
}

@Composable
fun PlayerInfo(
    player: PlayerDetails,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        DeadOverlay(!player.alive) {
            PlayerImage(
                imageSource = player.imageSource,
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.img_big))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoleVoteScreenPreviewVote() {
    KoinPreviewApplication(
        modules = { listOf(appModule) }
    ) {
        val voter = when (true) {
            true -> FakePlayersRepository.playerDetails.random()
            else -> FakePlayersRepository.playerDetails.first { it.role.roleType == RoleType.Cupido }
        }
        RoleVoteScreen(
            uiState = RoleVoteUiState(
                playersToVote = FakePlayersRepository.playerDetails.subList(0, 7).map {
                    if (it.id % 2 == 0) it.copy(
                        alive = false
                    ) else it
                },
                voter = voter
            ),
            canNavigateBack = false,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoleVoteScreenPreviewTimer() {
    KoinPreviewApplication(
        modules = { listOf(appModule) }
    ) {
        RoleVoteScreen(
            canVote = false,
            uiState = RoleVoteUiState(
                playersToVote = FakePlayersRepository.playerDetails,
                voter = FakePlayersRepository.playerDetails.random()
            ),
            canNavigateBack = false,
        )
    }
}