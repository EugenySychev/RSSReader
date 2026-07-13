package com.sychev.rss_reader.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.sychev.domain.model.RefreshInterval
import com.sychev.domain.repositories.SettingsRepository
import com.sychev.rss_reader.work.RefreshScheduler
import javax.inject.Inject

@HiltViewModel
internal class SettingsScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val refreshScheduler: RefreshScheduler,
) : ViewModel() {

    val uiState = combine(
        settingsRepository.isAutoRefreshEnabled(),
        settingsRepository.getRefreshInterval(),
    ) { enabled, interval ->
        SettingsScreenUiState(autoRefreshEnabled = enabled, refreshInterval = interval)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsScreenUiState(),
    )

    fun setAutoRefreshEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoRefreshEnabled(enabled)
            if (enabled) {
                refreshScheduler.schedule(settingsRepository.getRefreshInterval().first())
            } else {
                refreshScheduler.cancel()
            }
        }
    }

    fun setRefreshInterval(interval: RefreshInterval) {
        viewModelScope.launch {
            settingsRepository.setRefreshInterval(interval)
            if (settingsRepository.isAutoRefreshEnabled().first()) {
                refreshScheduler.schedule(interval)
            }
        }
    }
}
