package com.example.lupus_v2.ui.screens.game.game_round_result

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.lupus_v2.R
import com.example.lupus_v2.data.di.KoinPreviewApplication
import com.example.lupus_v2.data.di.appModule
import com.example.lupus_v2.data.fake.FakePlayersRepository
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.PlayerImageSource
import com.example.lupus_v2.model.manager.RoundResult
import com.example.lupus_v2.model.roles.AssassinKilledPlayerIsCupido
import com.example.lupus_v2.model.roles.AssassinKilledPlayers
import com.example.lupus_v2.model.roles.CitizienKilledPlayers
import com.example.lupus_v2.model.roles.SeduttriceSavedPlayer
import com.example.lupus_v2.model.roles.RoleFactory
import com.example.lupus_v2.model.roles.RoleType
import com.example.lupus_v2.model.roles.VeggenteDiscoverKiller
import com.example.lupus_v2.ui.commonui.LupusTopAppBar
import com.example.lupus_v2.ui.commonui.PlayerImage
import org.koin.compose.koinInject

@Composable
fun RoundResultScreen(
    navigateToNextScreen: () -> Unit = {},
    nextScreenText: String,
    roundResult: RoundResult,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    navigateUp: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.surface), // Background color
        topBar = {
            LupusTopAppBar(
                title = stringResource(id = R.string.round_result),
                canNavigateBack = canNavigateBack,
                navigateUp = navigateUp
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background) // Screen background
        ) {
            LazyColumn(
                modifier = Modifier
                    //.padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface) // List background
            ) {
                items(roundResult.events) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                        modifier = Modifier
                            .height(dimensionResource(id = R.dimen.roleCard_behavior_height))
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.padding_small))
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            RoleImage(
                                roleTypes = it.roleTypes,
                                modifier = Modifier
                                    .width(dimensionResource(id = R.dimen.img_big))
                            )
                            when (it) {
                                is AssassinKilledPlayers -> {
                                    PlayerEvent(
                                        players = listOf(it.playerKilled),
                                        hoverIconRes = R.drawable.baseline_not_interested_24,
                                        eventDescription = stringResource(
                                            id = R.string.role_event_assassin_killed_players,
                                            it.playerKilled.name
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                is AssassinKilledPlayerIsCupido -> {
                                    PlayerEvent(
                                        players = it.playersKilled.toList(),
                                        hoverIconRes = R.drawable.baseline_not_interested_24,
                                        eventDescription = stringResource(
                                            id = R.string.role_event_assassin_killed_player_is_cupido,
                                            it.playersKilled.first.name,
                                            it.playersKilled.second.name
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                is SeduttriceSavedPlayer ->
                                    PlayerEvent(
                                        players = listOf(it.playerSaved),
                                        hoverIconRes = R.drawable.baseline_shield_24,
                                        eventDescription = stringResource(
                                            id = R.string.role_event_facili_costumi_saved_player,
                                            it.playerSaved.name
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                is VeggenteDiscoverKiller ->
                                    PlayerEvent(
                                        players = listOf(it.killer),
                                        hoverIconRes = R.drawable.baseline_dangerous_24,
                                        eventDescription = stringResource(
                                            id = R.string.role_event_veggente_discover_killer,
                                            it.killer.name
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                is CitizienKilledPlayers ->
                                    PlayerEvent(
                                        players = listOf(it.playerKilled),
                                        hoverIconRes = R.drawable.baseline_not_interested_24,
                                        eventDescription = stringResource(
                                            id = R.string.role_event_cittadino_killed_players,
                                            it.playerKilled.name
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                            }
                        }
                    }
                }
            }
            Button(
                onClick = { navigateToNextScreen() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(dimensionResource(id = R.dimen.padding_small)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Button background
                    contentColor = MaterialTheme.colorScheme.onPrimary // Text color
                )
            ) {
                Text(text = stringResource(id = R.string.go_to_discuss))
            }
        }
    }
}

@Composable
fun RoleImage(
    roleTypes: List<RoleType>,
    modifier: Modifier = Modifier
) {
    val roleFactory: RoleFactory = koinInject()
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        for (roleType in roleTypes) {
            Image(
                painter = painterResource(id = roleFactory.createRole(roleType).image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun PlayerEvent(
    players: List<PlayerDetails>,
    @DrawableRes hoverIconRes: Int,
    eventDescription: String,
    modifier: Modifier = Modifier
) {
    val singlePlayer = players.size == 1
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier//.background(MaterialTheme.colorScheme.surface)
    ) {
        if (singlePlayer) {
            // Single player display
            PlayerDisplay(
                imageSource = players.first().imageSource,
                playerName = players.first().name,
                hoverIconRes = hoverIconRes,
                imageSize = dimensionResource(id = R.dimen.img_medium),
                modifier = Modifier.weight(0.5f)
            )
        } else {
            // Multiple player display
            LazyColumn(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(0.5f)
            ) {
                items(players, key = { it.id }) { player ->
                    PlayerDisplay(
                        imageSource = player.imageSource,
                        playerName = player.name,
                        hoverIconRes = hoverIconRes,
                        imageSize = dimensionResource(id = R.dimen.img_small)
                    )
                }
            }
        }
        //Spacer(modifier = Modifier.weight(0.1f))
        EventDescription(
            description = eventDescription,
            modifier = Modifier
                .weight(0.4f)
                .fillMaxSize()
        )
    }
}

@Composable
fun PlayerDisplay(
    imageSource: PlayerImageSource, // Adjust type based on your implementation
    playerName: String,
    @DrawableRes hoverIconRes: Int,
    imageSize: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxHeight()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_small))
        ) {
            PlayerImage(
                modifier = Modifier.size(imageSize),
                imageSource = imageSource,
            )
            Icon(
                painter = painterResource(id = hoverIconRes),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary // Icon color
            )
        }
        Text(
            text = playerName,
            color = MaterialTheme.colorScheme.onSurface, // Text color
            modifier = Modifier
        )
    }
}

@Composable
fun EventDescription(
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = description,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant, // Description text color
        )
    }
}


@Preview(showBackground = true)
@Composable
fun RoundResultScreenPreview() {
    var i = 0
    KoinPreviewApplication(
        modules = { listOf(appModule) }
    ) {
        RoundResultScreen(
            roundResult = RoundResult(
                events = listOf(
                    AssassinKilledPlayers(
                        playerKilled = PlayerDetails(
                            id = i++, name = "ciao"
                        )
                    ),
                    SeduttriceSavedPlayer(
                        playerSaved = PlayerDetails(id = i++, name = "Brody")
                    ),
                    VeggenteDiscoverKiller(
                        killer = PlayerDetails(id = i++, name = "Guarda Questa")
                    ),
                    AssassinKilledPlayerIsCupido(
                        playersKilled = Pair(
                            PlayerDetails(id = i++, name = "Che"),
                            PlayerDetails(id = i++, name = "Body")
                        )
                    )
                ),
                updatedPlayer = FakePlayersRepository.playerDetails,
            ),
            nextScreenText = stringResource(id = R.string.go_to_discuss)
        )
    }
}