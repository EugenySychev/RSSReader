package com.sychev.rss_reader.screens.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.sychev.domain.repositories.NewsRepository
import com.sychev.domain.repositories.SourceRepository
import com.sychev.rss_reader.navigation.Destination
import javax.inject.Inject

@HiltViewModel
internal class NewsScreenViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val sourceRepository: SourceRepository,
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(true)
    private val refreshError = MutableStateFlow<String?>(null)
    private val sortOrder = MutableStateFlow(NewsSortOrder.NEWEST_FIRST)
    private var navController: NavController? = null

    val uiState = combine(
        newsRepository.getAllNews(),
        isRefreshing,
        refreshError,
        sortOrder,
    ) { items, refreshing, error, order ->
        val sortedItems = when (order) {
            NewsSortOrder.NEWEST_FIRST -> items.sortedByDescending { it.pubDate }
            NewsSortOrder.OLDEST_FIRST -> items.sortedBy { it.pubDate }
        }
        NewsScreenUiState(
            isLoading = refreshing && items.isEmpty(),
            isRefreshing = refreshing,
            items = sortedItems,
            error = error.takeIf { items.isEmpty() },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NewsScreenUiState(isLoading = true),
    )

    init {
        refreshNews()
    }

    private fun refreshNews() {
        viewModelScope.launch {
            isRefreshing.value = true
            newsRepository.deleteOldNews()
            val sources = sourceRepository.getSources().first().filter { it.isEnabled }
            if (sources.isEmpty()) {
                refreshError.value = "No news sources yet. Add one from the Sources screen."
                isRefreshing.value = false
                return@launch
            }
            var lastError: String? = null
            for (source in sources) {
                try {
                    newsRepository.fetchLatestNews(source.url)
                } catch (e: Exception) {
                    lastError = e.message
                }
            }
            refreshError.value = lastError
            isRefreshing.value = false
        }
    }

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    fun onRefresh() {
        refreshNews()
    }

    fun onSortOrderChanged(order: NewsSortOrder) {
        sortOrder.value = order
    }

    fun markAsRead(link: String) {
        viewModelScope.launch { newsRepository.markAsRead(link) }
    }

    fun onAction(action: NewsScreenAction) {
        when (action) {
            is NewsScreenAction.OpenItem -> {
                navController?.navigate(Destination.NewsDetail.createRoute(action.item.link))
            }
            NewsScreenAction.ReadMore -> {
                refreshNews()
            }
        }
    }
}
