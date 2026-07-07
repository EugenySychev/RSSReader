package com.sychev.domain.repositories

import kotlinx.coroutines.flow.Flow
import com.sychev.domain.model.NewsItem

interface NewsRepository {

    fun getAllNews(): Flow<List<NewsItem>>

    suspend fun fetchLatestNews(url: String): List<NewsItem>

    suspend fun markAsRead(link: String)

    suspend fun deleteOldNews()
}
