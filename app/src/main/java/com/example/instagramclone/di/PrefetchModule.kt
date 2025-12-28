package com.example.instagramclone.di

import android.content.Context
import com.example.instagramclone.domain.prefetch.ImagePrefetchAlgo
import com.example.instagramclone.domain.prefetch.MediaPrefetch
import com.example.instagramclone.domain.prefetch.PerformanceMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrefetchModule {
    @Provides
    @Singleton
    fun providePerformanceMonitor(
        @ApplicationContext context: Context,
    ): PerformanceMonitor {
        return PerformanceMonitor(context)
    }

    @Provides
    @Singleton
    fun provideImagePrefetch(
        @ApplicationContext context: Context,
        performanceMonitor: PerformanceMonitor
    ): MediaPrefetch {
        return ImagePrefetchAlgo(context, performanceMonitor)
    }
}