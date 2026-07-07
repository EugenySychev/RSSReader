package com.sychev.data.mapper

import com.sychev.data.db.entity.SourceEntity
import com.sychev.domain.model.SourceItem

internal fun SourceEntity.toSourceItem(): SourceItem = SourceItem(
    id = id,
    name = name,
    url = url,
    description = description,
    imageUrl = imageUrl,
    isEnabled = isEnabled,
)
