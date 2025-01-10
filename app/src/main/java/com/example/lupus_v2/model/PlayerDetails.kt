package com.example.lupus_v2.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.lupus_v2.data.BitmapUtil
import com.example.lupus_v2.data.database.entity.Player
import com.example.lupus_v2.data.fake.FakeImageRepository
import com.example.lupus_v2.model.roles.Cittadino
import com.example.lupus_v2.model.roles.Role
import com.example.lupus_v2.model.roles.RoleFactory
import com.example.lupus_v2.model.roles.RoleType
import kotlinx.coroutines.runBlocking

sealed class PlayerImageSource {
    data class DrawableSource(@DrawableRes val resId: Int) : PlayerImageSource()
    data class UriSource(val uri: String) : PlayerImageSource() // Use String for simplicity
}

@Composable
fun PlayerImageSource.getPainter(): Painter {
    return when (this) {
        is PlayerImageSource.DrawableSource -> painterResource(id = this.resId)
        is PlayerImageSource.UriSource -> rememberAsyncImagePainter(model = this.uri) // returns an asyncImagePainter
    }
}


// This extension function will retrieve a Bitmap based on the source type.
fun PlayerImageSource.toBitmap(context: Context): Bitmap? {
    return when (this) {
        is PlayerImageSource.DrawableSource -> BitmapUtil.getBitmapFromDrawable(context, resId)
        is PlayerImageSource.UriSource -> BitmapUtil.getBitmapFromUriNonComposable(context, uri)
    }
}

data class PlayerDetails(
    val id: Int = 0,
    val name: String,
    val role: Role = Cittadino(),
    val alive: Boolean = true,
    val imageSource: PlayerImageSource = PlayerImageSource.DrawableSource(FakeImageRepository.defaultImages.random())
) {
    fun kill() = copy(alive = false)
}

fun PlayerDetails.toPlayer(context: Context): Player = Player(
    id = id,
    name = name,
    imageSource = {
        val bitmap = imageSource.toBitmap(context)
        // Use runBlocking to execute the coroutine and get the result
        val imageSourceLocation = runBlocking {
            BitmapUtil.saveBitmapToFile(context, bitmap!!, name)
        }
        imageSourceLocation // Return the imageSourceLocation
    }.toString()
)

fun Player.toPlayerDetails(): PlayerDetails = PlayerDetails(
    id = id,
    name = name,
    role = Cittadino(),
    alive = true,
    imageSource = PlayerImageSource.UriSource(imageSource)
)

@Composable
fun getBitmapFromUri(uri: String): Bitmap? {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(uri) {
        val imageLoader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(uri)
            .build()

        val result = (imageLoader.execute(request) as SuccessResult).drawable
        bitmap = (result as android.graphics.drawable.BitmapDrawable).bitmap
    }

    return bitmap
}
