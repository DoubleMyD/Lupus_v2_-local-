package com.example.lupus_v2.ui.screens.player.player_edit

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.lupus_v2.R
import com.example.lupus_v2.data.BitmapUtil.getBitmapFromDrawable
import com.example.lupus_v2.data.fake.FakeImageRepository
import com.example.lupus_v2.data.fake.FakePlayersRepository
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.PlayerImageSource
import com.example.lupus_v2.model.getBitmapFromUri
import com.example.lupus_v2.ui.commonui.CancelAndConfirmButtons
import com.example.lupus_v2.ui.commonui.LupusTopAppBar
import com.example.lupus_v2.ui.commonui.PlayerImage
import com.example.lupus_v2.ui.commonui.PlayerNameField

sealed class PlayerEditUiState {
    data class Success(val playerDetails: PlayerDetails) : PlayerEditUiState()
    data object Loading : PlayerEditUiState()
    data object Error : PlayerEditUiState()
}

@Composable
fun PlayerEditScreen(
    modifier: Modifier = Modifier,
    uiState: PlayerEditUiState,
    updatePlayer: (Int, String, Bitmap, Context) -> Unit = { _, _, _, _ -> },
    navigateBack: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        topBar = {
            LupusTopAppBar(
                title = stringResource(id = R.string.edit_player),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        }
    ) { innerPadding ->
        when (uiState) {
            is PlayerEditUiState.Loading -> {
                // Show a loading spinner or a placeholder
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(MaterialTheme.colorScheme.surface), // Screen background
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary // Loading spinner color
                    )
                }
            }

            is PlayerEditUiState.Error -> {
                // Show an error message
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(MaterialTheme.colorScheme.surface), // Screen background
                ) {
                    Text(
                        text = stringResource(id = R.string.error_loading_player),
                        color = MaterialTheme.colorScheme.error, // Error text color
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            is PlayerEditUiState.Success -> {
                // Render the editable player screen with loaded details
                val player = uiState.playerDetails
                PlayerContent(
                    player = player,
                    updatePlayer = { id, name, image, context ->
                        updatePlayer(id, name, image, context)
                        navigateBack()
                    },
                    onCancelClick = navigateBack,
                    innerPadding = innerPadding
                )
            }
        }
    }
}

@Composable
fun PlayerContent(
    player: PlayerDetails,
    updatePlayer: (Int, String, Bitmap, Context) -> Unit = { _, _, _, _ -> },
    onCancelClick: () -> Unit,
    innerPadding: PaddingValues
) {
    val initialUri = (player.imageSource as? PlayerImageSource.UriSource)?.uri?.let { Uri.parse(it) }
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf(player.name) }
    val maxNameLength = dimensionResource(id = R.dimen.name_max_length).value.toInt()
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(initialUri) }
    //var randomImage by remember { mutableIntStateOf(FakeImageRepository.defaultImages.random()) }

    // Initialize the photo picker
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> imageUri = uri }

    // Helper function to determine image source
    val imageSource = remember(imageUri) {
        if (imageUri != null) PlayerImageSource.UriSource(imageUri.toString())
        else player.imageSource
    }

    val bitmap =
        if (imageUri != null) {
            getBitmapFromUri(imageUri.toString())
        } else {
            getBitmapFromDrawable(context, FakeImageRepository.defaultImages.random())
        }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium)),
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        PlayerImage(
            imageSource = imageSource,
            onClick = {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)  //it tells what type of media you want to show (if only video, or only images and so on )
                )
            },
            showCameraIcon = true,
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.img_big))
                .background(MaterialTheme.colorScheme.surfaceVariant) // Image background
        )

        PlayerNameField(
            name = name,
            onValueChange = { if (it.length <= maxNameLength) name = it },
            maxNameLength = maxNameLength,
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
        )

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
        CancelAndConfirmButtons(
            onCancelClick = {
                onCancelClick()
//                name = player.name
//                imageUri = null
//                randomImage = FakeImageRepository.defaultImages.random()
            },
            onConfirmClick = {
                if (bitmap != null && name.isNotBlank()) {
                    updatePlayer(
                        player.id,
                        name,
                        bitmap,
                        context
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerEditScreenPreviewSuccess() {
    PlayerEditScreen(
        modifier = Modifier.fillMaxSize(),
        uiState = PlayerEditUiState.Success(FakePlayersRepository.playerDetails[0])
    )
}

@Preview(showBackground = true)
@Composable
fun PlayerEditScreenPreviewError() {
    PlayerEditScreen(
        modifier = Modifier.fillMaxSize(),
        uiState = PlayerEditUiState.Error
    )
}

@Preview(showBackground = true)
@Composable
fun PlayerEditScreenPreviewLoading() {
    PlayerEditScreen(
        modifier = Modifier.fillMaxSize(),
        uiState = PlayerEditUiState.Loading
    )
}
