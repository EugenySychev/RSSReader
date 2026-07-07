package com.sychev.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sychev.data.db.dao.NewsDao
import com.sychev.data.db.dao.SourceDao
import com.sychev.data.db.entity.NewsEntity
import com.sychev.data.db.entity.SourceEntity

@Database(
    entities = [NewsEntity::class, SourceEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDao
    abstract fun sourceDao(): SourceDao
}
