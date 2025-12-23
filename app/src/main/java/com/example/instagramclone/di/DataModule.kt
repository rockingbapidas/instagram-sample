package com.example.instagramclone.di

import android.content.Context
import androidx.room.Room
import com.example.instagramclone.data.local.AppDatabase
import com.example.instagramclone.data.local.dao.NotificationDao
import com.example.instagramclone.data.mapper.PostMapper
import com.example.instagramclone.data.remote.api.InstagramApi
import com.example.instagramclone.data.repository.PostRepositoryImpl
import com.example.instagramclone.data.repository.ProfileRepositoryImpl
import com.example.instagramclone.domain.repository.PostRepository
import com.example.instagramclone.domain.repository.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideInstagramApi(): InstagramApi =
        Retrofit.Builder()
            .baseUrl("https://your.api.url/") // TODO: Replace with real base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InstagramApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "instagram_db")
            .fallbackToDestructiveMigration() // TODO: Add proper migration
            .build()
} 