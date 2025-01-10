package com.example.lupus_v2.data.di

import com.example.lupus_v2.data.database.LupusDatabase
import com.example.lupus_v2.data.repository.OfflinePlayersRepository
import com.example.lupus_v2.data.repository.PlayersRepository
import com.example.lupus_v2.model.manager.GameManager
import com.example.lupus_v2.model.manager.PlayerManager
import com.example.lupus_v2.model.manager.RoundResultManager
import com.example.lupus_v2.model.manager.VoteManager
import com.example.lupus_v2.model.roles.RoleFactory
import com.example.lupus_v2.model.util.Randomizer
import com.example.lupus_v2.model.util.RangeValidator
import com.example.lupus_v2.ui.navigation.Destination
import com.example.lupus_v2.ui.navigation.setup.DefaultNavigator
import com.example.lupus_v2.ui.navigation.setup.Navigator
import com.example.lupus_v2.ui.screens.game.game_citizien_vote.CitizenVoteViewmodel
import com.example.lupus_v2.ui.screens.game.game_prepare.choose_players.ChoosePlayersViewModel
import com.example.lupus_v2.ui.screens.game.game_prepare.choose_role.ChooseRoleViewModel
import com.example.lupus_v2.ui.screens.game.game_role_vote.RoleVoteViewModel
import com.example.lupus_v2.ui.screens.list.list_archive.ListsArchiveViewModel
import com.example.lupus_v2.ui.screens.player.player_edit.PlayerEditViewModel
import com.example.lupus_v2.ui.screens.player.player_new.NewPlayerViewModel
import com.example.lupus_v2.ui.screens.player.player_archive.PlayersArchiveViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<Navigator> { DefaultNavigator(Destination.Home) }
    single<PlayersRepository> {
        OfflinePlayersRepository(LupusDatabase.getDatabase(get()).playerDao())
    }

    single<PlayerManager> { PlayerManager() }
    single<RoleFactory> { RoleFactory(get()) } // Pass PlayerManager to RoleFactory
    single<VoteManager> { VoteManager(get()) }
    single<RoundResultManager> { RoundResultManager( get(), get()) }
    single<GameManager> { GameManager(get(), get(), get(), get()) }

    single<Randomizer> { Randomizer() }
    single<RangeValidator> { RangeValidator() }
    viewModel { PlayersArchiveViewModel(playersRepository = get()) }
    viewModel { PlayerEditViewModel(playersRepository = get()) }
    viewModel { NewPlayerViewModel(playersRepository = get()) }
    viewModel { ListsArchiveViewModel(playersListRepository = get()) }
    viewModel { ChooseRoleViewModel(gameManager = get(), rangeValidator = get(), randomizer = get()) }
    viewModel { ChoosePlayersViewModel(get()) }
    viewModel { RoleVoteViewModel(get()) }
    viewModel { CitizenVoteViewmodel(get(), get()) }


}