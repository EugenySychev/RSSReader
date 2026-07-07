package com.sychev.rss_reader.screens.news

import com.sychev.domain.model.NewsItem

internal data class NewsScreenUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val items: List<NewsItem> = emptyList(),
    val error: String? = null,
)
