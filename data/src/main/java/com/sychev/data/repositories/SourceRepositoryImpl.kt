package com.sychev.data.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import com.sychev.data.db.dao.SourceDao
import com.sychev.data.db.entity.SourceEntity
import com.sychev.data.mapper.toSourceItem
import com.sychev.data.parser.RssFeedFetcher
import com.sychev.domain.model.SourceItem
import com.sychev.domain.repositories.SourceRepository
import javax.inject.Inject

internal class SourceRepositoryImpl @Inject constructor(
    private val sourceDao: SourceDao,
    private val feedFetcher: RssFeedFetcher,
) : SourceRepository {

    override fun getSources(): Flow<List<SourceItem>> =
        sourceDao.getAllSources().map { entities -> entities.map { it.toSourceItem() } }

    override suspend fun addSource(url: String): Boolean = withContext(Dispatchers.IO) {
        val feed = try {
            feedFetcher.fetch(url)
        } catch (e: Exception) {
            null
        }
        val name = feed?.title?.ifBlank { null } ?: url
        val insertedId = sourceDao.insert(
            SourceEntity(name = name, url = url, description = null, imageUrl = feed?.imageUrl),
        )
        insertedId != -1L
    }

    override suspend fun deleteSource(source: SourceItem): Unit = withContext(Dispatchers.IO) {
        sourceDao.delete(
            SourceEntity(
                id = source.id,
                name = source.name,
                url = source.url,
                description = source.description,
                imageUrl = source.imageUrl,
                isEnabled = source.isEnabled,
            ),
        )
    }

    override suspend fun setEnabled(source: SourceItem, enabled: Boolean): Unit = withContext(Dispatchers.IO) {
        sourceDao.setEnabled(source.id, enabled)
    }
}
