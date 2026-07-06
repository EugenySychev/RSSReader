package org.sychev.data.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.sychev.data.db.dao.SourceDao
import org.sychev.data.db.entity.SourceEntity
import org.sychev.data.mapper.toSourceItem
import org.sychev.domain.model.SourceItem
import org.sychev.domain.repositories.SourceRepository
import javax.inject.Inject

internal class SourceRepositoryImpl @Inject constructor(
    private val sourceDao: SourceDao,
) : SourceRepository {

    override fun getSources(): Flow<List<SourceItem>> =
        sourceDao.getAllSources().map { entities -> entities.map { it.toSourceItem() } }

    override suspend fun addSource(name: String, url: String): Boolean = withContext(Dispatchers.IO) {
        val insertedId = sourceDao.insert(
            SourceEntity(name = name, url = url, description = null, imageUrl = null),
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
