package org.sychev.rssreadernew.screens.news

import org.sychev.domain.model.NewsItem

internal data class NewsScreenUiState(
    val isLoading: Boolean = false,
    val items: List<NewsItem> = emptyList(),
    val error: String? = null,
)
