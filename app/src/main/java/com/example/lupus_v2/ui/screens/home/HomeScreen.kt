package com.example.lupus_v2.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lupus_v2.R
import com.example.lupus_v2.ui.commonui.LupusTopAppBar

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToSettings: () -> Unit,
    navigateToGame: () -> Unit,
    navigateToLoadGame: () -> Unit,
    navigateToPlayersArchive: () -> Unit
){
    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.surface), // Background color
        topBar = {
            LupusTopAppBar(
                title = stringResource(id = R.string.app_name),
                canNavigateBack = false,
                actions = {
                    IconButton(onClick = { navigateToSettings() }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer // Icon color
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface), // Screen background
            contentAlignment = Alignment.Center
        ) {
            //background image
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            //content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Button(
                    onClick = { navigateToGame() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary, // Button background
                        contentColor = MaterialTheme.colorScheme.onPrimary // Text color
                    )
                ) {
                    Text(text = stringResource(id = R.string.start_new_game))
                }

                Spacer(modifier = Modifier.height(8.dp))

                ElevatedButton(
                    onClick = { navigateToLoadGame() },
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer, // Button background
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer // Text color
                    )
                ) {
                    Text(text = stringResource(id = R.string.load_game))
                }

                Spacer(modifier = Modifier.height(32.dp))

                ElevatedButton(
                    onClick = { navigateToPlayersArchive() },
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary, // Button background
                        contentColor = MaterialTheme.colorScheme.onSecondary // Text color
                    )
                ) {
                    Text(text = stringResource(id = R.string.players_archive))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        modifier = Modifier.fillMaxSize(),
        navigateToSettings = {},
        navigateToGame = {},
        navigateToLoadGame = {},
        navigateToPlayersArchive = {}
    )
}