package com.example.instagramclone.domain.repository

import com.example.instagramclone.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getAllNotifications(): Flow<List<Notification>>
    fun getUnreadNotifications(): Flow<List<Notification>>
    fun getUnreadCount(): Flow<Int>
    suspend fun saveNotification(notification: Notification)
    suspend fun markAsRead(notificationId: String)
    suspend fun markAllAsRead()
    suspend fun deleteNotification(notificationId: String)
}

