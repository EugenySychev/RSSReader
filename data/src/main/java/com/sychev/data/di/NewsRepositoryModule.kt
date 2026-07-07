package com.sychev.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.sychev.data.repositories.NewsRepositoryImpl
import com.sychev.domain.repositories.NewsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NewsRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNewsRepository(impl: NewsRepositoryImpl): NewsRepository
}
