package com.sychev.rss_reader.screens.sources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.sychev.domain.model.SourceItem
import com.sychev.domain.repositories.SourceRepository
import javax.inject.Inject

@HiltViewModel
internal class SourcesScreenViewModel @Inject constructor(
    private val sourceRepository: SourceRepository,
) : ViewModel() {

    private val error = MutableStateFlow<String?>(null)

    val uiState = combine(
        sourceRepository.getSources(),
        error,
    ) { sources, error ->
        SourcesScreenUiState(isLoading = false, sources = sources, error = error)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SourcesScreenUiState(isLoading = true),
    )

    fun addSource(name: String, url: String) {
        val trimmedUrl = url.trim()
        if (trimmedUrl.isBlank()) {
            error.value = "Enter an RSS URL"
            return
        }
        viewModelScope.launch {
            val added = sourceRepository.addSource(
                name = name.trim().ifBlank { trimmedUrl },
                url = trimmedUrl,
            )
            error.value = if (added) null else "This source is already added"
        }
    }

    fun deleteSource(source: SourceItem) {
        viewModelScope.launch { sourceRepository.deleteSource(source) }
    }

    fun setEnabled(source: SourceItem, enabled: Boolean) {
        viewModelScope.launch { sourceRepository.setEnabled(source, enabled) }
    }
}
