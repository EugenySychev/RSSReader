package org.sychev.data.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.sychev.data.db.dao.NewsDao
import org.sychev.data.db.dao.SourceDao
import org.sychev.data.db.entity.SourceEntity
import org.sychev.data.mapper.toNewsEntity
import org.sychev.data.mapper.toNewsItem
import org.sychev.data.parser.RssParser
import org.sychev.domain.model.NewsItem
import org.sychev.domain.repositories.NewsRepository
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

internal class NewsRepositoryImpl @Inject constructor(
    private val newsDao: NewsDao,
    private val sourceDao: SourceDao,
) : NewsRepository {

    private val parser = RssParser()

    override fun getAllNews(): Flow<List<NewsItem>> =
        newsDao.getAllNews().map { entities -> entities.map { it.toNewsItem() } }

    override suspend fun fetchLatestNews(url: String): List<NewsItem> = withContext(Dispatchers.IO) {
        val items = parseFeed(url)
        val sourceId = getOrCreateSourceId(url)
        val entities = items.map { item ->
            val existing = newsDao.getByLink(item.link)
            item.toNewsEntity(
                id = existing?.id ?: 0,
                sourceId = sourceId,
                isRead = existing?.isRead ?: false,
            )
        }
        newsDao.upsertAll(entities)
        items
    }

    private fun parseFeed(url: String): List<NewsItem> {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connectTimeout = 10_000
        connection.readTimeout = 30_000
        return try {
            parser.parse(connection.inputStream)
        } finally {
            connection.disconnect()
        }
    }

    private suspend fun getOrCreateSourceId(url: String): Long {
        sourceDao.getByUrl(url)?.let { return it.id }
        val insertedId = sourceDao.insert(SourceEntity(name = url, url = url, description = null, imageUrl = null))
        if (insertedId != -1L) return insertedId
        return sourceDao.getByUrl(url)?.id ?: error("Failed to resolve source for $url")
    }
}
