package com.example.instagramclone.domain.model

data class Notification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean,
    val actionData: NotificationActionData? = null
)

enum class NotificationType {
    LIKE,
    UNLIKE,
    COMMENT,
    FOLLOW,
    UNFOLLOW,
    MENTION,
    // Add more types as needed
}

data class NotificationActionData(
    val postId: String? = null,
    val userId: String? = null,
    val commentId: String? = null,
    val actionType: String? = null
)

