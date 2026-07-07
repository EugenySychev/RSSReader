package com.sychev.data.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import com.sychev.data.db.dao.NewsDao
import com.sychev.data.db.dao.SourceDao
import com.sychev.data.db.entity.SourceEntity
import com.sychev.data.mapper.toNewsEntity
import com.sychev.data.mapper.toNewsItem
import com.sychev.data.parser.RssFeedFetcher
import com.sychev.domain.model.NewsItem
import com.sychev.domain.repositories.NewsRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private val MAX_NEWS_AGE_MILLIS = TimeUnit.DAYS.toMillis(30)

internal class NewsRepositoryImpl @Inject constructor(
    private val newsDao: NewsDao,
    private val sourceDao: SourceDao,
    private val feedFetcher: RssFeedFetcher,
) : NewsRepository {

    override fun getAllNews(): Flow<List<NewsItem>> =
        newsDao.getAllNews().map { entities -> entities.map { it.toNewsItem() } }

    override suspend fun fetchLatestNews(url: String): List<NewsItem> = withContext(Dispatchers.IO) {
        val feed = feedFetcher.fetch(url)
        val sourceId = getOrCreateSourceId(url, feed.imageUrl)
        val entities = feed.items.map { item ->
            val existing = newsDao.getByLink(item.link)
            item.toNewsEntity(
                id = existing?.id ?: 0,
                sourceId = sourceId,
            )
        }
        newsDao.upsertAll(entities)
        feed.items
    }

    override suspend fun markAsRead(link: String): Unit = withContext(Dispatchers.IO) {
        val existing = newsDao.getByLink(link) ?: return@withContext
        if (!existing.isRead) {
            newsDao.markAsRead(existing.id)
        }
    }

    override suspend fun deleteOldNews(): Unit = withContext(Dispatchers.IO) {
        val threshold = System.currentTimeMillis() - MAX_NEWS_AGE_MILLIS
        newsDao.deleteOlderThan(threshold)
    }

    private suspend fun getOrCreateSourceId(url: String, imageUrl: String?): Long {
        sourceDao.getByUrl(url)?.let { existing ->
            if (imageUrl != null && existing.imageUrl != imageUrl) {
                sourceDao.update(existing.copy(imageUrl = imageUrl))
            }
            return existing.id
        }
        val insertedId = sourceDao.insert(SourceEntity(name = url, url = url, description = null, imageUrl = imageUrl))
        if (insertedId != -1L) return insertedId
        return sourceDao.getByUrl(url)?.id ?: error("Failed to resolve source for $url")
    }
}
