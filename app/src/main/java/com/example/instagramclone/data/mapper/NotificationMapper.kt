package com.example.instagramclone.data.mapper

import com.example.instagramclone.data.local.entities.NotificationEntity
import com.example.instagramclone.domain.model.Notification
import com.example.instagramclone.domain.model.NotificationActionData
import com.example.instagramclone.domain.model.NotificationType

object NotificationMapper {
    fun toDomain(entity: NotificationEntity): Notification {
        return Notification(
            id = entity.id,
            type = NotificationType.valueOf(entity.type),
            title = entity.title,
            message = entity.message,
            timestamp = entity.timestamp,
            isRead = entity.isRead,
            actionData = NotificationActionData(
                postId = entity.postId,
                userId = entity.userId,
                commentId = entity.commentId,
                actionType = entity.actionType
            )
        )
    }

    fun toEntity(domain: Notification): NotificationEntity {
        return NotificationEntity(
            id = domain.id,
            type = domain.type.name,
            title = domain.title,
            message = domain.message,
            timestamp = domain.timestamp,
            isRead = domain.isRead,
            postId = domain.actionData?.postId,
            userId = domain.actionData?.userId,
            commentId = domain.actionData?.commentId,
            actionType = domain.actionData?.actionType
        )
    }
}

