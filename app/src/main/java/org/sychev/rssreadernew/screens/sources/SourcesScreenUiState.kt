package org.sychev.rssreadernew.screens.sources

import org.sychev.domain.model.SourceItem

internal data class SourcesScreenUiState(
    val isLoading: Boolean = false,
    val sources: List<SourceItem> = emptyList(),
    val error: String? = null,
)
