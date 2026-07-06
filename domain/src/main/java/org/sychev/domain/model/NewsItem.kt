package org.sychev.domain.model

data class NewsItem(
    val title: String,
    val description: String,
    val link: String,
    val imageUrl: String?,
    val pubDate: Long,
    val author: String?,
    val category: String?,
)
