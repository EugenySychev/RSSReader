package org.sychev.data.mapper

import org.sychev.data.db.entity.NewsEntity
import org.sychev.domain.model.NewsItem

internal fun NewsEntity.toNewsItem(): NewsItem = NewsItem(
    title = title,
    description = description.orEmpty(),
    link = link,
    imageUrl = imageUrl,
    pubDate = pubDate,
    author = author,
    category = category,
)

internal fun NewsItem.toNewsEntity(id: Long, sourceId: Long, isRead: Boolean): NewsEntity = NewsEntity(
    id = id,
    sourceId = sourceId,
    title = title,
    description = description,
    content = null,
    link = link,
    imageUrl = imageUrl,
    pubDate = pubDate,
    author = author,
    category = category,
    isRead = isRead,
)
