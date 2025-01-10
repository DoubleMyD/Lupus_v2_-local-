package com.example.lupus_v2.ui.commonui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.lupus_v2.R

@Composable
fun CancelAndConfirmButtons(
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        ElevatedButton(
            onClick = { onCancelClick() },
            colors = ButtonDefaults.elevatedButtonColors(containerColor = MaterialTheme.colorScheme.secondary),
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.cancel_button),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondary,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { onConfirmClick() },
            colors = ButtonDefaults.elevatedButtonColors(containerColor = MaterialTheme.colorScheme.primary),
        ) {
            Text(
                maxLines = 1,
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.confirm_button),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }

}