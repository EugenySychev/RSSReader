package com.sychev.data.mapper

import com.sychev.data.db.entity.NewsEntity
import com.sychev.domain.model.NewsItem

internal fun NewsEntity.toNewsItem(): NewsItem = NewsItem(
    title = title,
    description = description.orEmpty(),
    link = link,
    sourceUrl = sourceUrl,
    imageUrl = imageUrl,
    pubDate = pubDate,
    author = author,
    category = category,
    isRead = isRead,
)

internal fun NewsItem.toNewsEntity(id: Long, sourceId: Long): NewsEntity = NewsEntity(
    id = id,
    sourceId = sourceId,
    title = title,
    description = description,
    content = null,
    link = link,
    sourceUrl = sourceUrl,
    imageUrl = imageUrl,
    pubDate = pubDate,
    author = author,
    category = category,
    isRead = isRead,
)
