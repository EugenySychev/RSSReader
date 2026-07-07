package com.sychev.domain.model

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class NewsItem(
    val title: String,
    val description: String,
    val link: String,
    val sourceUrl: String,
    val imageUrl: String?,
    val pubDate: Long,
    val author: String?,
    val category: String?,
    val isRead: Boolean
)

fun NewsItem.getLocalTimeString(): String {
    val instant = Instant.ofEpochMilli(pubDate)
    val localZone = ZoneId.systemDefault()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return instant.atZone(localZone).format(formatter)
}