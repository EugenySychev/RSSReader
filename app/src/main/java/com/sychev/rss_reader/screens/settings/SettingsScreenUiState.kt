package com.sychev.rss_reader.screens.settings

import com.sychev.domain.model.RefreshInterval

internal data class SettingsScreenUiState(
    val autoRefreshEnabled: Boolean = false,
    val refreshInterval: RefreshInterval = RefreshInterval.HOUR_1,
)
