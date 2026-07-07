package com.sychev.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.sychev.data.repositories.SourceRepositoryImpl
import com.sychev.domain.repositories.SourceRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SourceRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSourceRepository(impl: SourceRepositoryImpl): SourceRepository
}
