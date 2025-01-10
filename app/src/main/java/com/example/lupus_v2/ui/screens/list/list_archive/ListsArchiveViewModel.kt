package com.example.lupus_v2.ui.screens.list.list_archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupus_v2.data.database.entity.PlayersList
import com.example.lupus_v2.data.repository.PlayersListsRepository
import com.example.lupus_v2.model.PlayerDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListsArchiveViewModel(
    private val playersListRepository: PlayersListsRepository
) : ViewModel() {

    // Improved flow with loading and error handling
    val databaseLists: StateFlow<ListsArchiveUiState> = flow {
        emit(ListsArchiveUiState.Loading)  // Start with loading state
        try {
            val lists = playersListRepository.getAllListsWithPlayersDetails()
            emit(ListsArchiveUiState.Success(lists))
        } catch (e: Exception) {
            emit(ListsArchiveUiState.Error)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = ListsArchiveUiState.Loading
    )

    fun addNewList(listName: String) {
        viewModelScope.launch {
            playersListRepository.insertPlayersList(PlayersList(name = listName))
        }

    }

    companion object {
        private const val TIMEOUT_MILLIS = 10_000L
    }
}