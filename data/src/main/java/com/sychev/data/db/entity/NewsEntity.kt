package com.sychev.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "news",
    foreignKeys = [
        ForeignKey(
            entity = SourceEntity::class,
            parentColumns = ["id"],
            childColumns = ["source_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("source_id"), Index(value = ["link"], unique = true)]
)
data class NewsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "source_id")
    val sourceId: Long,
    val title: String,
    val description: String?,
    val content: String?,
    val link: String,
    @ColumnInfo(name = "source_url")
    val sourceUrl: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String?,
    @ColumnInfo(name = "pub_date")
    val pubDate: Long,
    val author: String?,
    val category: String?,
    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false
)
