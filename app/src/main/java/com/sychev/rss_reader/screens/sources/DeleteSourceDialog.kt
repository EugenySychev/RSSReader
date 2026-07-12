package com.sychev.rss_reader.screens.sources

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.sychev.domain.model.SourceItem
import com.sychev.rss_reader.R

@Composable
internal fun DeleteSourceDialog(
    source: SourceItem,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        content = {
            Column(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.medium,
                    )
                    .padding(dimensionResource(R.dimen.add_source_padding)),
            ) {
                Text(stringResource(R.string.delete_source))
                Text(
                    stringResource(
                        id = R.string.delete_source_question,
                        formatArgs = arrayOf(source.name),
                    )
                )
                Row(
                    modifier = Modifier
                        .padding(top = dimensionResource(R.dimen.add_source_padding)),
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(onClick = onConfirm) {
                        Text(stringResource(R.string.delete))
                    }
                }
            }
        }
    )
}