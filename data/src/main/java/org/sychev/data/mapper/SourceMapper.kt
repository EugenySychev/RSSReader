package org.sychev.data.mapper

import org.sychev.data.db.entity.SourceEntity
import org.sychev.domain.model.SourceItem

internal fun SourceEntity.toSourceItem(): SourceItem = SourceItem(
    id = id,
    name = name,
    url = url,
    description = description,
    imageUrl = imageUrl,
    isEnabled = isEnabled,
)
