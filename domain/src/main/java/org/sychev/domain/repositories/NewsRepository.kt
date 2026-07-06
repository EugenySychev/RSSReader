package org.sychev.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.sychev.domain.model.NewsItem

interface NewsRepository {

    fun getAllNews(): Flow<List<NewsItem>>

    suspend fun fetchLatestNews(url: String): List<NewsItem>
}
