package com.example.lupus_v2.ui.screens.player.player_new

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupus_v2.data.BitmapUtil
import com.example.lupus_v2.data.database.entity.Player
import com.example.lupus_v2.data.repository.PlayersRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class NewPlayerViewModel(
    private val playersRepository: PlayersRepository,
) : ViewModel() {
    /**
     * Add a new player to the game.
     * return false if the name is not available or already taken, true if the player is added successfully
     */
    fun savePlayer(context: Context, playerName: String, image: Bitmap) {
        viewModelScope.launch {
            val name = playerName.trim()
            if (!isNameAvailable(name)) {
                Log.d("NewPlayerViewModel", "Name not available")
                //_eventChannel.send(NewPlayerEvent.ErrorNameNotAvailable)
            } else {
                val saved = savePlayerImage(context, name, image)
                if (saved) {
                    Log.d("NewPlayerViewModel", "Player saved successfully")
                }//_eventChannel.send(NewPlayerEvent.PlayerSaved)
            }
        }
    }

    private suspend fun isNameAvailable(name: String): Boolean {
        return playersRepository.getPlayerByName(name).firstOrNull() == null
    }

    private suspend fun savePlayerImage(
        context: Context,
        playerName: String,
        image: Bitmap
    ): Boolean {
        return try {
            val path = BitmapUtil.saveBitmapToFile(context, image, playerName)
            val player = Player(name = playerName, imageSource = path)
            playersRepository.insertPlayer(player)
            true
        } catch (e: Exception) {
            false
        }
    }
}