package com.example.lupus_v2.ui.commonui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.lupus_v2.R
import com.example.lupus_v2.data.di.KoinPreviewApplication
import com.example.lupus_v2.data.di.appModule
import com.example.lupus_v2.data.fake.FakePlayersRepository
import com.example.lupus_v2.model.PlayerImageSource
import com.example.lupus_v2.model.getPainter

/**
 * the clip shape is "medium"
 * the correct shape for nested round corners is : InnerRadius = OuterRadius - OuterThickness/2
 * -InnerRadius = il raggio dell'angolo del componente figlio
 * -OuterRadius = il raggio dell'angolo del componente padre
 * -OuterThickness = la distanza tra il padre e il figlio (il padding)
 */
@Composable
fun PlayerImage(
    imageSource: PlayerImageSource,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    padding: Int? = null,
    colorFilter: ColorFilter? = null,
    showCameraIcon: Boolean = false,
    borderColor: Color = Color.Transparent
) {
    val eventModifier = when (onClick) {
        null -> Modifier
        else -> Modifier.clickable(onClick = onClick)
    }

    val realPadding = when (padding) {
        null -> dimensionResource(id = R.dimen.padding_small)
        else -> padding.dp
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(realPadding)
    ) {
        Image(
            painter = imageSource.getPainter(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = colorFilter,
            modifier = Modifier
                .fillMaxSize()
                .then(eventModifier)
                .clip(MaterialTheme.shapes.medium)
                .border(BorderStroke(dimensionResource(id = R.dimen.border_small), borderColor), shape = MaterialTheme.shapes.medium)

        )
        if (showCameraIcon) {
            Icon(
                painter = painterResource(id = R.drawable.outline_photo_camera_24),
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.75f),
            )
        }
    }
}

@Composable
fun PlayerNameField(
    name: String,
    modifier: Modifier = Modifier,
    maxNameLength: Int = 64,
    onValueChange: (String) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center//spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = name,
            placeholder = { Text(text = stringResource(id = R.string.insert_name)) },
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            singleLine = true,
            modifier = Modifier.weight(0.8f)
        )
        Text(
            textAlign = TextAlign.Center,
            text = "${name.length}/$maxNameLength",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.weight(0.2f).padding(start = dimensionResource(id = R.dimen.padding_small))
        )
    }
}

@Composable
fun DeadOverlay(
    showOverlay: Boolean = true,
    overlayShape: CornerBasedShape = MaterialTheme.shapes.large,
    content: @Composable () -> Unit
){
    Box (
        Modifier.pointerInput(Unit) {
        // Capture no pointer interactions so clicks pass through
    }
    ){
        content()
        if(showOverlay) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .zIndex(10f)
                    .background(
                        colorResource(R.color.player_dead_overlay).copy(
                            0.8f
                        ),
                        shape = overlayShape
                    )
                    .pointerInput(Unit) {
                        // Capture no pointer interactions so clicks pass through
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerImagePreview() {
    KoinPreviewApplication(
        modules = { listOf(appModule) }
    ) {
        PlayerImage(FakePlayersRepository.playerDetails.first().imageSource)
    }
}