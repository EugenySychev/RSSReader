package com.sychev.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.sychev.domain.model.RefreshInterval
import com.sychev.domain.repositories.SettingsRepository
import javax.inject.Inject

private val AUTO_REFRESH_ENABLED = booleanPreferencesKey("auto_refresh_enabled")
private val REFRESH_INTERVAL_MINUTES = longPreferencesKey("refresh_interval_minutes")

internal class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    override fun isAutoRefreshEnabled(): Flow<Boolean> =
        dataStore.data.map { it[AUTO_REFRESH_ENABLED] ?: false }

    override fun getRefreshInterval(): Flow<RefreshInterval> =
        dataStore.data.map { prefs ->
            val minutes = prefs[REFRESH_INTERVAL_MINUTES]
            RefreshInterval.entries.firstOrNull { it.minutes == minutes } ?: RefreshInterval.HOUR_1
        }

    override suspend fun setAutoRefreshEnabled(enabled: Boolean) {
        dataStore.edit { it[AUTO_REFRESH_ENABLED] = enabled }
    }

    override suspend fun setRefreshInterval(interval: RefreshInterval) {
        dataStore.edit { it[REFRESH_INTERVAL_MINUTES] = interval.minutes }
    }
}
