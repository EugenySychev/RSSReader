package org.sychev.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import org.sychev.data.db.dao.NewsDao
import org.sychev.data.db.dao.SourceDao
import org.sychev.data.db.entity.NewsEntity
import org.sychev.data.db.entity.SourceEntity

@Database(
    entities = [NewsEntity::class, SourceEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDao
    abstract fun sourceDao(): SourceDao
}
