package com.example.instagramclone.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.instagramclone.data.local.converters.NotificationTypeConverter

@Entity(tableName = "notifications")
@TypeConverters(NotificationTypeConverter::class)
data class NotificationEntity(
    @PrimaryKey val id: String,
    val type: String, // NotificationType as String
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean,
    val postId: String? = null,
    val userId: String? = null,
    val commentId: String? = null,
    val actionType: String? = null
)

