package com.example.lupus_v2.ui.screens.game.game_phone_pass

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.lupus_v2.R
import com.example.lupus_v2.data.fake.FakePlayersRepository
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.ui.commonui.LupusTopAppBar
import com.example.lupus_v2.ui.commonui.PlayerImage


@Composable
fun PhonePassScreen(
    nextPlayer: PlayerDetails,
    modifier: Modifier = Modifier,
    onConfirmButtonClicked: () -> Unit = {},
) {
    var phonePassed by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        modifier = modifier,
        topBar = {
            LupusTopAppBar(
                title = stringResource(id = R.string.phone_pass),
                canNavigateBack = false
            )
        }
    ) { innerPadding ->
        val string = if (phonePassed) {
            stringResource(id = R.string.next_player)
        } else {
            stringResource(id = R.string.phone_pass_description)
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            PlayerSection(
                player = nextPlayer,
                onImageClick = { phonePassed = true },
                phonePassed = phonePassed,
                modifier = Modifier.size(dimensionResource(id = R.dimen.img_big))
            )

            Text(
                text = string,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )
            if (phonePassed) {
                Button(
                    onClick = { onConfirmButtonClicked() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.show_role),
                        //color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerSection(
    player: PlayerDetails,
    modifier: Modifier = Modifier,
    onImageClick: () -> Unit = {},
    phonePassed: Boolean = false,
) {
    Card(
        onClick = onImageClick,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            PlayerImage(
                imageSource = player.imageSource,
                borderColor = if (phonePassed) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                //onClick = onImageClick,
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.img_big))
            )
            Text(
                text = player.name,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PhonePassScreenPreview() {
    PhonePassScreen(
        nextPlayer = FakePlayersRepository.playerDetails.random()
    )
}