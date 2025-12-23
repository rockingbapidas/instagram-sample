package com.example.instagramclone.di

import com.example.instagramclone.data.remote.api.InstagramApi
import com.example.instagramclone.data.repository.ProfileRepositoryImpl
import com.example.instagramclone.domain.repository.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {
    @Provides
    @Singleton
    fun provideProfileRepository(
        api: InstagramApi
    ): ProfileRepository = ProfileRepositoryImpl(
        api = api
    )
}