package com.sychev.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import com.sychev.data.db.entity.NewsEntity

@Dao
interface NewsDao {

    @Query("SELECT * FROM news ORDER BY pub_date DESC")
    fun getAllNews(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE source_id = :sourceId ORDER BY pub_date DESC")
    fun getNewsBySource(sourceId: Long): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE id = :id")
    suspend fun getById(id: Long): NewsEntity?

    @Query("SELECT * FROM news WHERE link = :link LIMIT 1")
    suspend fun getByLink(link: String): NewsEntity?

    @Query("SELECT * FROM news WHERE is_read = 0 ORDER BY pub_date DESC")
    fun getUnreadNews(): Flow<List<NewsEntity>>

    @Upsert
    suspend fun upsert(news: NewsEntity)

    @Upsert
    suspend fun upsertAll(newsList: List<NewsEntity>)

    @Update
    suspend fun update(news: NewsEntity)

    @Delete
    suspend fun delete(news: NewsEntity)

    @Query("UPDATE news SET is_read = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE news SET is_read = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM news WHERE source_id = :sourceId")
    suspend fun deleteBySource(sourceId: Long)

    @Query("DELETE FROM news WHERE pub_date < :threshold")
    suspend fun deleteOlderThan(threshold: Long)
}
