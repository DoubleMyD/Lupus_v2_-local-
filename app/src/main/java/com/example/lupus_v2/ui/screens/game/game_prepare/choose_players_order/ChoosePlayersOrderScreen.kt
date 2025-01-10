package com.example.lupus_v2.ui.screens.game.game_prepare.choose_players_order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.lupus_v2.R
import com.example.lupus_v2.data.fake.FakePlayersRepository
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.ui.commonui.LupusTopAppBar
import com.example.lupus_v2.ui.commonui.PlayerImage
import com.example.lupus_v2.ui.commonui.Village

@Composable
fun ChoosePlayersOrderScreen(
    players: List<PlayerDetails>,
    onOrderUpdate: (List<Int>) -> Unit = {},
    onCenterIconClick: () -> Unit,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    navigateUp: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            LupusTopAppBar(
                title = stringResource(id = R.string.choose_players),
                canNavigateBack = canNavigateBack,
                navigateUp = navigateUp
            )
        }
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Village(
                players = players,
                onOrderChanged = onOrderUpdate,
                modifier = Modifier.fillMaxSize()
            ) { player ->
                Card(
                    //colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .fillMaxSize()
                        //.padding(dimensionResource(id = R.dimen.padding_small))
                ) {
                    PlayerImage(
                        imageSource = player.imageSource,
                        padding = 0,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }
            CenterIcon(
                onClick = onCenterIconClick,
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.img_small))
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
fun CenterIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.large
            )
            .padding(dimensionResource(id = R.dimen.padding_small))
    ) {
        Icon(
            imageVector = Icons.Filled.Done,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChoosePlayersOrderScreenPreview() {
    ChoosePlayersOrderScreen(
        players = FakePlayersRepository.playerDetails.subList(0, 8),
        onCenterIconClick = {}
    )
}