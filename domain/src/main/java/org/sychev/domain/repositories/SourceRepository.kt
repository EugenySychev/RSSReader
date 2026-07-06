package org.sychev.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.sychev.domain.model.SourceItem

interface SourceRepository {

    fun getSources(): Flow<List<SourceItem>>

    suspend fun addSource(name: String, url: String): Boolean

    suspend fun deleteSource(source: SourceItem)

    suspend fun setEnabled(source: SourceItem, enabled: Boolean)
}
