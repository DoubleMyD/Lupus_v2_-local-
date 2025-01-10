package com.example.lupus_v2.ui.screens.player.player_edit

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupus_v2.data.BitmapUtil
import com.example.lupus_v2.data.database.entity.Player
import com.example.lupus_v2.data.fake.FakeImageRepository
import com.example.lupus_v2.data.repository.PlayersRepository
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.PlayerImageSource
import com.example.lupus_v2.model.toPlayer
import com.example.lupus_v2.model.toPlayerDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlayerEditViewModel(
    private val playersRepository: PlayersRepository
) : ViewModel() {
    // Expose the UI state
    private val _uiState = MutableStateFlow<PlayerEditUiState>(PlayerEditUiState.Loading)
    val uiState: StateFlow<PlayerEditUiState> = _uiState

    fun loadPlayer(playerId: Int) {
        _uiState.value = PlayerEditUiState.Loading // Set loading state initially

        viewModelScope.launch {
            try {
                val loadedPlayer = playersRepository.getPlayerStream(playerId).first()
                    ?.toPlayerDetails() ?: PlayerDetails(
                    id = -1,
                    name = "I do not exist!",
                    imageSource = PlayerImageSource.DrawableSource(FakeImageRepository.defaultBlankImage)
                )
                _uiState.value = PlayerEditUiState.Success(loadedPlayer) // Set success state
            } catch (e: Exception) {
                _uiState.value = PlayerEditUiState.Error // Set error state on exception
            }
        }
    }

    suspend fun updatePlayer(
        playerId: Int,
        newName: String,
        newBitmap: Bitmap,
        context: Context
    ): Boolean {
        return try {
            val path = BitmapUtil.saveBitmapToFile(context, newBitmap, newName)
            val updatedPlayer = Player(id = playerId, name = newName, imageSource = path)
            playersRepository.updatePlayer(updatedPlayer)
            true
        } catch (e: Exception) {
            false
        }
    }

}