package com.example.instagramclone.di

import com.example.instagramclone.data.local.AppDatabase
import com.example.instagramclone.data.local.dao.NotificationDao
import com.example.instagramclone.data.repository.NotificationRepositoryImpl
import com.example.instagramclone.domain.notification.NotificationHandler
import com.example.instagramclone.domain.notification.NotificationHandlerRegistry
import com.example.instagramclone.domain.notification.handlers.LikeNotificationHandler
import com.example.instagramclone.domain.notification.handlers.UnlikeNotificationHandler
// Uncomment when you want to enable Follow/Unfollow notifications
// import com.example.instagramclone.domain.notification.handlers.FollowNotificationHandler
// import com.example.instagramclone.domain.notification.handlers.UnfollowNotificationHandler
import com.example.instagramclone.domain.repository.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideNotificationRepository(
        db: AppDatabase,
    ): NotificationRepository {
        return NotificationRepositoryImpl(db.notificationDao())
    }

    @Provides
    @Singleton
    fun provideNotificationHandlers(
        likeHandler: LikeNotificationHandler,
        unlikeHandler: UnlikeNotificationHandler
        // To add a new notification type:
        // 1. Create a handler class implementing NotificationHandler
        // 2. Add it as a parameter here
        // 3. Add it to the setOf() below
        // Example: followHandler: FollowNotificationHandler
    ): Set<NotificationHandler> {
        return setOf(
            likeHandler,
            unlikeHandler
            // Add new handlers here, e.g.:
            // followHandler,
            // unfollowHandler
        )
    }

    @Provides
    @Singleton
    fun provideNotificationHandlerRegistry(
        handlers: Set<@JvmSuppressWildcards NotificationHandler>
    ): NotificationHandlerRegistry {
        return NotificationHandlerRegistry(handlers)
    }
}

