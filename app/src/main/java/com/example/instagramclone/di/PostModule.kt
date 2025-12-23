package com.example.instagramclone.di

import com.example.instagramclone.data.local.AppDatabase
import com.example.instagramclone.data.mapper.PostMapper
import com.example.instagramclone.data.remote.api.InstagramApi
import com.example.instagramclone.data.repository.PostRepositoryImpl
import com.example.instagramclone.domain.repository.PostRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PostModule {
    @Provides
    @Singleton
    fun providePostMapper(): PostMapper = PostMapper

    @Provides
    @Singleton
    fun providePostRepository(
        api: InstagramApi,
        db: AppDatabase,
        postMapper: PostMapper
    ): PostRepository = PostRepositoryImpl(
        api = api,
        db = db,
        postDao = db.postDao(),
        postMapper = postMapper
    )
}