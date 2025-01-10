package com.example.lupus_v2.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.lupus_v2.R
import com.example.lupus_v2.model.manager.GameManager
import com.example.lupus_v2.model.manager.PlayerManager
import com.example.lupus_v2.ui.screens.game.game_citizien_vote.CitizenVoteScreen
import com.example.lupus_v2.ui.screens.game.game_citizien_vote.CitizenVoteViewmodel
import com.example.lupus_v2.ui.screens.game.game_discussion.DiscussionScreen
import com.example.lupus_v2.ui.screens.game.game_over.GameOverScreen
import com.example.lupus_v2.ui.screens.game.game_phone_pass.PhonePassScreen
import com.example.lupus_v2.ui.screens.game.game_prepare.choose_players.ChoosePlayersScreen
import com.example.lupus_v2.ui.screens.game.game_prepare.choose_players.ChoosePlayersViewModel
import com.example.lupus_v2.ui.screens.game.game_prepare.choose_players_order.ChoosePlayersOrderScreen
import com.example.lupus_v2.ui.screens.game.game_prepare.choose_role.ChooseRoleScreen
import com.example.lupus_v2.ui.screens.game.game_prepare.choose_role.ChooseRoleViewModel
import com.example.lupus_v2.ui.screens.game.game_role_vote.RoleVoteScreen
import com.example.lupus_v2.ui.screens.game.game_role_vote.RoleVoteViewModel
import com.example.lupus_v2.ui.screens.game.game_round_result.RoundResultScreen
import com.example.lupus_v2.ui.screens.player.player_archive.PlayersArchiveViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

sealed interface GameDestination : Destination {
    @Serializable
    data object Vote : GameDestination

    @Serializable
    data object RoundResult : GameDestination

    @Serializable
    data class PassPhone(val goToNextPlayer: Boolean = false) : GameDestination

    @Serializable
    data object ChooseRole : GameDestination

    @Serializable
    data object ChoosePlayers : GameDestination

    @Serializable
    data object PlayerOrder : GameDestination

    @Serializable
    data object Discussion : GameDestination

    @Serializable
    data object CitizenVote : GameDestination

    @Serializable
    data object CitizenVoteSummary : GameDestination

    @Serializable
    data object GameOver : GameDestination
}

fun NavGraphBuilder.gameNavigation(
    navController: NavHostController,
) {
    composable<GameDestination.ChoosePlayers> {
        val playersArchiveViewModel: PlayersArchiveViewModel = koinInject()
        val databasePlayers by playersArchiveViewModel.databasePlayers.collectAsState()

        val choosePlayersViewModel: ChoosePlayersViewModel = koinInject()
        val uiState by choosePlayersViewModel.uiState.collectAsState()


        ChoosePlayersScreen(
            players = databasePlayers.players,
            uiState = uiState,
            onPlayerClick = { player ->
                choosePlayersViewModel.togglePlayerSelected(player)
            },
            onConfirmPlayersClick = {
                if (choosePlayersViewModel.confirmPlayers()) {
                    navController.navigate(GameDestination.ChooseRole)
                    true
                } else {
                    false
                }
            },
            modifier = Modifier.fillMaxSize(),
            canNavigateBack = true,
            navigateUp = { navController.navigateUp() }
        )
    }

    composable<GameDestination.ChooseRole> {
        val chooseRoleViewModel: ChooseRoleViewModel = koinInject()
        val uiState by chooseRoleViewModel.uiState.collectAsState()

        ChooseRoleScreen(
            uiState = uiState,
            onSwitchMode = { isRandom -> chooseRoleViewModel.switchUiState(isRandom) },
            onIncrementButtonClick = { roleType -> chooseRoleViewModel.incrementCount(roleType) },
            onDecrementButtonClick = { roleType -> chooseRoleViewModel.decrementCount(roleType) },
            onCheckedChange = { roleType -> chooseRoleViewModel.toggleRoleSelected(roleType) },
            onConfirmClick = {
                val playerSizeOk = chooseRoleViewModel.confirmRoles()
                if (playerSizeOk) {
                    navController.navigate(GameDestination.PlayerOrder)
                    true
                } else {
                    false
                }
            },
            modifier = Modifier.fillMaxSize(),
            canNavigateBack = true,
            navigateUp = { navController.navigateUp() }
        )
    }

    composable<GameDestination.PlayerOrder> {
        val gameManager: GameManager = koinInject()
        val playerManager: PlayerManager = koinInject()
        //val players by playerManager.players.collectAsState()
        //val players = playerManager.players.value
        val players by playerManager.players.collectAsState()

        ChoosePlayersOrderScreen(
            players = players,
            onCenterIconClick = {
                gameManager.startRound()
                navController.navigate(GameDestination.PassPhone(false))
            },
            onOrderUpdate = { newOrder ->
                playerManager.updateOrder(newOrder)
            },
        )
    }



    composable<GameDestination.PassPhone> {
        val args = it.toRoute<GameDestination.PassPhone>()
        val goToNextPlayer by remember { derivedStateOf { args.goToNextPlayer } }
        val playerManager: PlayerManager = koinInject()
        var currentPlayer by remember { mutableStateOf(playerManager.currentPlayer) }

        LaunchedEffect(goToNextPlayer) {
            if (goToNextPlayer) {
                playerManager.goToNextPlayer()
                currentPlayer = playerManager.currentPlayer
            }
        }

        PhonePassScreen(
            nextPlayer = currentPlayer,
            onConfirmButtonClicked = { navController.navigate(GameDestination.Vote) },
            modifier = Modifier.fillMaxSize()
        )
    }

    composable<GameDestination.Vote> {
        val gameManager: GameManager = koinInject()
        val playerManager: PlayerManager = koinInject()
        val roleVoteViewModel: RoleVoteViewModel = koinInject()
        val uiState by roleVoteViewModel.uiState.collectAsState()


        val currentPlayer by remember { mutableStateOf(playerManager.currentPlayer) }
        val canVote by remember { mutableStateOf(gameManager.canVote(currentPlayer)) }

        LaunchedEffect(currentPlayer) {
            val voter = currentPlayer
            roleVoteViewModel.initVote(
                voter = voter,
                playerToVote = playerManager.players.value.filter { it != voter }
            )
        }

        RoleVoteScreen(
            uiState = uiState,
            canVote = canVote,
            onTimerFinished = {
                if (gameManager.isLastPlayerOfRound(uiState.voter)) {
                    gameManager.performRoundResult()
                    navController.navigate(GameDestination.RoundResult)
                } else {
                    navController.navigate(GameDestination.PassPhone(true))
                }
            },
            clearPlayerToBounce = { player -> roleVoteViewModel.removePlayerToBounce(player) },
            onVoteClick = { player -> roleVoteViewModel.togglePlayerVoted(player) },
            onConfirmVoteClick = { votedPlayers ->
                val result = roleVoteViewModel.onConfirmVoteClick(votedPlayers)
                val playerFinishedVoting = result.getOrNull()
                playerFinishedVoting?.let {
                    if (!it) {
                        roleVoteViewModel.resetVotedPlayers()
                        false
                    } else {
                        if (gameManager.isLastPlayerOfRound(uiState.voter)) {
                            gameManager.performRoundResult()
                            navController.navigate(GameDestination.RoundResult)
                            true
                        } else {
                            navController.navigate(GameDestination.PassPhone(true))
                            true
                        }
                    }
                }
                    ?: run {
                        roleVoteViewModel.resetVotedPlayers()
                        false
                    }
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    composable<GameDestination.RoundResult> {
        val gameManager: GameManager = koinInject()

        val roundResult = remember { gameManager.getLastRoundResult() }

        RoundResultScreen(
            roundResult = roundResult,
            navigateToNextScreen = {
                navController.navigate(GameDestination.Discussion)
            },
            nextScreenText = stringResource(id = R.string.go_to_discuss),
            modifier = Modifier.fillMaxSize()
        )
    }

    composable<GameDestination.Discussion> {
        val playerManager: PlayerManager = koinInject()
        val players = playerManager.players.value

        DiscussionScreen(
            players = players,
            onTimerFinished = {
                playerManager.resetIndex()
                navController.navigate(GameDestination.CitizenVote)
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    composable<GameDestination.CitizenVote> {

        val gameManager: GameManager = koinInject()
        val playerManager: PlayerManager = koinInject()
        val citizenVoteViewmodel: CitizenVoteViewmodel = koinInject()
        val players by playerManager.players.collectAsState()
        val uiState by citizenVoteViewmodel.uiState.collectAsState()

        var currentPlayer by remember { mutableStateOf(playerManager.currentPlayer) }

        LaunchedEffect(players) {
            citizenVoteViewmodel.updateVoterPlayer(currentPlayer)
        }

        CitizenVoteScreen(
            uiState = uiState,
            players = players,
            votePlayer = { votedPlayer ->
                val result = citizenVoteViewmodel.votePlayer(
                    currentPlayer, votedPlayer
                ).getOrNull()
                result?.let {
                    if (gameManager.isLastPlayerOfRound(uiState.voter)) {
                        gameManager.performCitizenRoundResult()
                        navController.navigate(GameDestination.CitizenVoteSummary)
                        return@CitizenVoteScreen
                    } else {
                        playerManager.goToNextPlayer()
                        currentPlayer = playerManager.currentPlayer
                        citizenVoteViewmodel.updateVoterPlayer(
                            newVoter = currentPlayer,
                        )
                    }
                }
                    ?: run {
                        //citizenVoteViewmodel.triggerPlayerNotValidAnimation(votedPlayer)
                    }
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    composable<GameDestination.CitizenVoteSummary> {
        val gameManager: GameManager = koinInject()

        val roundResult = remember { gameManager.getLastRoundResult() }

        RoundResultScreen(
            roundResult = roundResult,
            navigateToNextScreen = {
                if (gameManager.gameIsOver()) {
                    navController.navigate(GameDestination.GameOver)
                } else {
                    gameManager.startRound()
                    navController.navigate(GameDestination.PassPhone(false))
                }
            },
            nextScreenText = stringResource(id = R.string.start_new_round),
            modifier = Modifier.fillMaxSize()
        )
    }

    composable<GameDestination.GameOver> {
        val gameManager: GameManager = koinInject()
        val playerManager: PlayerManager = koinInject()
        val winnerRole = remember { gameManager.getWinnersRole() }
        val winnerPlayers =
            remember { playerManager.players.value.filter { it.role.roleType == winnerRole } }

        GameOverScreen(
            winnerRole = winnerRole,
            winnerPlayers = winnerPlayers,
            goToHome = {
                gameManager.reset()
                navController.navigate(Destination.Home)
            }
        )

    }
}