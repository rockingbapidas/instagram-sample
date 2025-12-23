package com.example.instagramclone.presentation.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
// import com.example.instagramclone.R // Uncomment when you have notification icon
import com.example.instagramclone.domain.model.Notification
import com.example.instagramclone.domain.notification.NotificationHandlerRegistry
import com.example.instagramclone.presentation.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val handlerRegistry: NotificationHandlerRegistry
) {
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Instagram Notifications",
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for likes, comments, follows, etc."
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(notification: Notification) {
        val handler = handlerRegistry.getHandler(notification.type)

        val intent = handler?.getHandleIntent(context, notification) ?: Intent(
            context,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notificationId", notification.id)
            putExtra("notificationType", notification.type.name)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notification.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = handler?.getDefaultTitle(notification) ?: notification.title
        val message = handler?.getDefaultMessage(notification) ?: notification.message

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app icon
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        notificationManager.notify(notification.id.hashCode(), notificationBuilder.build())
    }

    fun handleNotificationAction(notification: Notification) {
        val handler = handlerRegistry.getHandler(notification.type)
        handler?.handleAction(context, notification)
    }

    companion object {
        private const val CHANNEL_ID = "instagram_notifications"
    }
}

