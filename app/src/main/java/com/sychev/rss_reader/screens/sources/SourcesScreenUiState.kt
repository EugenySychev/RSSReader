package com.sychev.rss_reader.screens.sources

import com.sychev.domain.model.SourceItem

internal data class SourcesScreenUiState(
    val isLoading: Boolean = false,
    val sources: List<SourceItem> = emptyList(),
    val error: String? = null,
)
