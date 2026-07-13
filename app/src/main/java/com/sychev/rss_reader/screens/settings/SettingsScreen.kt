package com.sychev.rss_reader.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sychev.domain.model.RefreshInterval

@Composable
internal fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Auto-refresh sources", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Periodically fetch new items in the background",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = uiState.autoRefreshEnabled,
                        onCheckedChange = viewModel::setAutoRefreshEnabled,
                    )
                }

                if (uiState.autoRefreshEnabled) {
                    Text(
                        text = "Refresh interval",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                    )
                    RefreshInterval.entries.forEach { interval ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = interval == uiState.refreshInterval,
                                    onClick = { viewModel.setRefreshInterval(interval) },
                                    role = Role.RadioButton,
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = interval == uiState.refreshInterval,
                                onClick = null,
                            )
                            Text(
                                text = interval.label(),
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun RefreshInterval.label(): String = when (this) {
    RefreshInterval.MIN_15 -> "15 minutes"
    RefreshInterval.MIN_30 -> "30 minutes"
    RefreshInterval.HOUR_1 -> "1 hour"
    RefreshInterval.HOUR_2 -> "2 hours"
    RefreshInterval.HOUR_6 -> "6 hours"
    RefreshInterval.HOUR_12 -> "12 hours"
    RefreshInterval.HOUR_24 -> "24 hours"
}
