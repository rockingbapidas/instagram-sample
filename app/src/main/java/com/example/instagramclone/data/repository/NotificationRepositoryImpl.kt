package com.example.instagramclone.data.repository

import com.example.instagramclone.data.local.dao.NotificationDao
import com.example.instagramclone.data.mapper.NotificationMapper
import com.example.instagramclone.domain.model.Notification
import com.example.instagramclone.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : NotificationRepository {

    override fun getAllNotifications(): Flow<List<Notification>> {
        return notificationDao.getAllNotifications().map { entities ->
            entities.map { NotificationMapper.toDomain(it) }
        }
    }

    override fun getUnreadNotifications(): Flow<List<Notification>> {
        return notificationDao.getUnreadNotifications().map { entities ->
            entities.map { NotificationMapper.toDomain(it) }
        }
    }

    override fun getUnreadCount(): Flow<Int> {
        return notificationDao.getUnreadCount()
    }

    override suspend fun saveNotification(notification: Notification) {
        notificationDao.insertNotification(NotificationMapper.toEntity(notification))
    }

    override suspend fun markAsRead(notificationId: String) {
        notificationDao.markAsRead(notificationId)
    }

    override suspend fun markAllAsRead() {
        notificationDao.markAllAsRead()
    }

    override suspend fun deleteNotification(notificationId: String) {
        notificationDao.deleteNotification(notificationId)
    }
}

