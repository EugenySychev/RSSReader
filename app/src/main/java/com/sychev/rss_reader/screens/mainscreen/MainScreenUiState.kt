package com.sychev.rss_reader.screens.mainscreen

import androidx.navigation.NavDestination
import com.sychev.domain.model.SourceItem
import com.sychev.rss_reader.navigation.Destination
import com.sychev.rss_reader.screens.news.NewsScreen

internal data class MainScreenUiState(
    val sourceList: List<SourceItem> = listOf(),
    val currentRoute: String? = Destination.News.route,
)