package com.example.instagramclone.domain.notification.handlers

import android.content.Context
import android.content.Intent
import com.example.instagramclone.R
import com.example.instagramclone.domain.model.Notification
import com.example.instagramclone.domain.model.NotificationType
import com.example.instagramclone.domain.notification.NotificationHandler
import com.example.instagramclone.presentation.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Example handler for FOLLOW notifications
 * This demonstrates how easy it is to add new notification types
 */
class FollowNotificationHandler @Inject constructor(
    @ApplicationContext private val context: Context
) : NotificationHandler {
    override val notificationType: NotificationType = NotificationType.FOLLOW

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
        // Navigate to the user's profile when user taps on follow notification
        getHandleIntent(context, notification)?.let { intent ->
            context.startActivity(intent)
        }
    }

    override fun getDefaultTitle(notification: Notification): String {
        return notification.title.ifEmpty { context.getString(R.string.notification_title_follow) }
    }

    override fun getDefaultMessage(notification: Notification): String {
        return notification.message.ifEmpty {
            context.getString(R.string.notification_message_follow)
        }
    }
}
