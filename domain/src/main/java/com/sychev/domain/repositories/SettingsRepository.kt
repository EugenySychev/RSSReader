package com.sychev.domain.repositories

import kotlinx.coroutines.flow.Flow
import com.sychev.domain.model.RefreshInterval

interface SettingsRepository {

    fun isAutoRefreshEnabled(): Flow<Boolean>

    fun getRefreshInterval(): Flow<RefreshInterval>

    suspend fun setAutoRefreshEnabled(enabled: Boolean)

    suspend fun setRefreshInterval(interval: RefreshInterval)
}
