package com.example.instagramclone.domain.notification.handlers

import android.content.Context
import android.content.Intent
import com.example.instagramclone.domain.model.Notification
import com.example.instagramclone.domain.model.NotificationType
import com.example.instagramclone.domain.notification.NotificationHandler
import com.example.instagramclone.presentation.MainActivity
import javax.inject.Inject

/**
 * Example handler for UNFOLLOW notifications
 */
class UnfollowNotificationHandler @Inject constructor() : NotificationHandler {
    override val notificationType: NotificationType = NotificationType.UNFOLLOW

    override fun getHandleIntent(context: Context, notification: Notification): Intent? {
        return notification.actionData?.userId?.let { userId ->
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("userId", userId)
                putExtra("navigateTo", "profile")
            }
        }
    }

    override fun handleAction(context: Context, notification: Notification) {
        // Navigate to the user's profile when user taps on unfollow notification
        getHandleIntent(context, notification)?.let { intent ->
            context.startActivity(intent)
        }
    }

    override fun getDefaultTitle(notification: Notification): String {
        return notification.title.ifEmpty { "Unfollow" }
    }

    override fun getDefaultMessage(notification: Notification): String {
        return notification.message.ifEmpty {
            "Someone unfollowed you"
        }
    }
}

