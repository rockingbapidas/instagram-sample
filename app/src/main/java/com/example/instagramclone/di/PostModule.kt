package com.example.instagramclone.di

import com.example.instagramclone.data.local.AppDatabase
import com.example.instagramclone.data.local.preferences.FeedPreferences
import com.example.instagramclone.data.mapper.PostMapper
import com.example.instagramclone.data.remote.api.PostApi
import com.example.instagramclone.data.repository.PostRepositoryImpl
import com.example.instagramclone.domain.repository.PostRepository
import com.example.instagramclone.domain.usecase.post.*
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
    fun providePostRepository(
        api: PostApi,
        db: AppDatabase,
        feedPreferences: FeedPreferences
    ): PostRepository = PostRepositoryImpl(
        api = api,
        db = db,
        feedPreferences = feedPreferences
    )

    @Provides
    fun provideGetPostsUseCase(postRepository: PostRepository) = GetPostsUseCase(postRepository)

    @Provides
    fun provideGetUserPostsUseCase(postRepository: PostRepository) = GetUserPostsUseCase(postRepository)

    @Provides
    fun provideCreatePostUseCase(postRepository: PostRepository) = CreatePostUseCase(postRepository)

    @Provides
    fun provideLikePostUseCase(postRepository: PostRepository) = LikePostUseCase(postRepository)

    @Provides
    fun provideSearchPostsUseCase(postRepository: PostRepository) = SearchPostsUseCase(postRepository)
}