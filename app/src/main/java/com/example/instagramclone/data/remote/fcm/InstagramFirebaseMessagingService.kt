package com.example.instagramclone.data.remote.fcm

import android.util.Log
import com.example.instagramclone.domain.model.Notification
import com.example.instagramclone.domain.model.NotificationActionData
import com.example.instagramclone.domain.model.NotificationType
import com.example.instagramclone.domain.repository.NotificationRepository
import com.example.instagramclone.presentation.notification.NotificationManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class InstagramFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }

        // Check if message contains notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            handleNotificationMessage(it, remoteMessage.data)
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        try {
            val notification = parseNotificationFromData(data)
            CoroutineScope(Dispatchers.IO).launch {
                notificationRepository.saveNotification(notification)
                notificationManager.showNotification(notification)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling data message", e)
        }
    }

    private fun handleNotificationMessage(
        notification: RemoteMessage.Notification,
        data: Map<String, String>
    ) {
        try {
            val domainNotification = parseNotificationFromRemote(notification, data)
            CoroutineScope(Dispatchers.IO).launch {
                notificationRepository.saveNotification(domainNotification)
                notificationManager.showNotification(domainNotification)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling notification message", e)
        }
    }

    private fun parseNotificationFromData(data: Map<String, String>): Notification {
        return Notification(
            id = data["id"] ?: System.currentTimeMillis().toString(),
            type = NotificationType.valueOf(data["type"] ?: NotificationType.LIKE.name),
            title = data["title"] ?: "",
            message = data["message"] ?: "",
            timestamp = data["timestamp"]?.toLongOrNull() ?: System.currentTimeMillis(),
            isRead = false,
            actionData = NotificationActionData(
                postId = data["postId"],
                userId = data["userId"],
                commentId = data["commentId"],
                actionType = data["actionType"]
            )
        )
    }

    private fun parseNotificationFromRemote(
        remoteNotification: RemoteMessage.Notification,
        data: Map<String, String>
    ): Notification {
        return Notification(
            id = data["id"] ?: System.currentTimeMillis().toString(),
            type = NotificationType.valueOf(data["type"] ?: NotificationType.LIKE.name),
            title = remoteNotification.title ?: data["title"] ?: "",
            message = remoteNotification.body ?: data["message"] ?: "",
            timestamp = data["timestamp"]?.toLongOrNull() ?: System.currentTimeMillis(),
            isRead = false,
            actionData = NotificationActionData(
                postId = data["postId"],
                userId = data["userId"],
                commentId = data["commentId"],
                actionType = data["actionType"]
            )
        )
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        // Send token to your backend server
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        // TODO: Implement token registration with your backend
        Log.d(TAG, "Token to be sent to server: $token")
    }

    companion object {
        private const val TAG = "FCMService"
    }
}

