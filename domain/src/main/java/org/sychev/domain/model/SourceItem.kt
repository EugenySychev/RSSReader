package org.sychev.domain.model

data class SourceItem(
    val id: Long,
    val name: String,
    val url: String,
    val description: String?,
    val imageUrl: String?,
    val isEnabled: Boolean,
)
