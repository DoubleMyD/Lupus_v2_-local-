package com.example.lupus_v2.ui.commonui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import com.example.lupus_v2.R

@Composable
fun FAB_Column_Search_Add(
    modifier: Modifier = Modifier,
    onSearchButtonClick: () -> Unit = {},
    onAddButtonClick: () -> Unit = {}
){
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium)),
        modifier = modifier
            .padding(
                end = WindowInsets.safeDrawing.asPaddingValues()
                    .calculateEndPadding(LocalLayoutDirection.current)
            )
    ) {
//        FloatingActionButton(
//            onClick = onSearchButtonClick,
//            containerColor = MaterialTheme.colorScheme.secondary,
//            contentColor = MaterialTheme.colorScheme.onSecondary,
//                    shape = MaterialTheme.shapes.large,
//        ) {
//            Icon(
//                imageVector = Icons.Default.Search,
//                contentDescription = null,
//            )
//        }
        FloatingActionButton(
            onClick = onAddButtonClick,
            shape = MaterialTheme.shapes.large,
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
            )
        }
    }
}