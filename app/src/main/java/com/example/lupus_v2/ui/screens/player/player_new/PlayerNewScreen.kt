package com.example.lupus_v2.ui.screens.player.player_new

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.lupus_v2.R
import com.example.lupus_v2.data.BitmapUtil.getBitmapFromDrawable
import com.example.lupus_v2.data.fake.FakeImageRepository
import com.example.lupus_v2.model.PlayerImageSource
import com.example.lupus_v2.model.getBitmapFromUri
import com.example.lupus_v2.ui.commonui.CancelAndConfirmButtons
import com.example.lupus_v2.ui.commonui.LupusTopAppBar
import com.example.lupus_v2.ui.commonui.PlayerImage
import com.example.lupus_v2.ui.commonui.PlayerNameField

@Composable
fun PlayerNewScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    onConfirmClick: (Context, String, Bitmap) -> Unit
) {
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    val maxNameLength = dimensionResource(id = R.dimen.name_max_length).value.toInt()
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var randomImage by remember { mutableIntStateOf(FakeImageRepository.defaultImages.random()) }

    // Initialize the photo picker
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> imageUri = uri }

    // Define source for player image
    val imageSource = getPlayerImageSource(imageUri)

    val bitmap = if (imageUri != null) {
        getBitmapFromUri(imageUri.toString())
    } else {
        getBitmapFromDrawable(context, FakeImageRepository.defaultImages.random())
    }

    Scaffold(
        topBar = {
            LupusTopAppBar(
                title = stringResource(id = R.string.add_player),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        modifier = modifier.background(MaterialTheme.colorScheme.surface) // Screen background
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium)),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface) // Main content background
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
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surfaceVariant) // Image background
            )
            PlayerNameField(
                name = name,
                onValueChange = { if (it.length <= maxNameLength) name = it },
                maxNameLength = maxNameLength,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant) // Input field background
                    .padding(dimensionResource(id = R.dimen.padding_small))
            )

            Spacer(Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

            CancelAndConfirmButtons(
                onCancelClick = {
                    name = ""
                    imageUri = null
                    randomImage = FakeImageRepository.defaultBlankImage
                },
                onConfirmClick = {
                    if(name.isNotEmpty()){
                        bitmap?.let {bitmap -> onConfirmClick(context, name, bitmap)}
                        navigateBack()
                    }
                }
            )
        }
    }
}

// Helper function to determine the player image source
@Composable
private fun getPlayerImageSource(imageUri: Uri?): PlayerImageSource {
    return imageUri?.let { PlayerImageSource.UriSource(it.toString()) }
        ?: PlayerImageSource.DrawableSource(FakeImageRepository.defaultBlankImage)
}

@Preview(showBackground = true)
@Composable
fun PlayerNewScreenPreview() {
    PlayerNewScreen(
        navigateBack = {},
        onConfirmClick = { _, _, _ -> }
    )
}

