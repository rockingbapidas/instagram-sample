package com.example.instagramclone.di

import com.example.instagramclone.data.repository.AdRepositoryImpl
import com.example.instagramclone.domain.repository.AdRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdModule {
    @Provides
    @Singleton
    fun provideAdRepository(): AdRepository {
        return AdRepositoryImpl()
    }
}