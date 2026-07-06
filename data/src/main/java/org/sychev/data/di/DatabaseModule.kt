package org.sychev.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.sychev.data.db.AppDatabase
import org.sychev.data.db.dao.NewsDao
import org.sychev.data.db.dao.SourceDao
import javax.inject.Singleton

private const val DATABASE_NAME = "rss_reader.db"

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()

    @Provides
    fun provideNewsDao(database: AppDatabase): NewsDao = database.newsDao()

    @Provides
    fun provideSourceDao(database: AppDatabase): SourceDao = database.sourceDao()
}
