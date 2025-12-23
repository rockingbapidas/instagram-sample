package com.example.instagramclone.domain.notification

import android.content.Context
import android.content.Intent
import com.example.instagramclone.domain.model.Notification
import com.example.instagramclone.domain.model.NotificationType

/**
 * Base interface for handling different types of notifications
 * This pattern allows easy extension for new notification types
 */
interface NotificationHandler {
    /**
     * The notification type this handler can process
     */
    val notificationType: NotificationType

    /**
     * Get the intent to handle the notification action when user taps on it
     * @param context The application context
     * @param notification The notification that was tapped
     */
    fun getHandleIntent(context: Context, notification: Notification): Intent?

    /**
     * Handle the notification action when user taps on it
     * @param context The application context
     * @param notification The notification that was tapped
     */
    fun handleAction(context: Context, notification: Notification)

    /**
     * Get the default title for this notification type
     */
    fun getDefaultTitle(notification: Notification): String

    /**
     * Get the default message for this notification type
     */
    fun getDefaultMessage(notification: Notification): String
}

