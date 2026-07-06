package org.sychev.rssreadernew.screens.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.sychev.domain.repositories.NewsRepository
import org.sychev.domain.repositories.SourceRepository
import javax.inject.Inject

@HiltViewModel
internal class NewsScreenViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val sourceRepository: SourceRepository,
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(true)
    private val refreshError = MutableStateFlow<String?>(null)

    val uiState = combine(
        newsRepository.getAllNews(),
        isRefreshing,
        refreshError,
    ) { items, refreshing, error ->
        NewsScreenUiState(
            isLoading = refreshing && items.isEmpty(),
            items = items,
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
}
