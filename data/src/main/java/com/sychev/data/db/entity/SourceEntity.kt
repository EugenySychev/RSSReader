package com.sychev.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sources",
    indices = [Index(value = ["url"], unique = true)]
)
data class SourceEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val url: String,
    val description: String?,

    @ColumnInfo(name = "image_url")
    val imageUrl: String?,

    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = true
)
