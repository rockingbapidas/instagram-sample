package com.example.instagramclone.domain.notification

import com.example.instagramclone.domain.model.NotificationType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Registry that maps notification types to their handlers
 * To add a new notification type, just create a handler and register it here
 */
@Singleton
class NotificationHandlerRegistry @Inject constructor(
    private val handlers: Set<@JvmSuppressWildcards NotificationHandler>
) {
    private val handlerMap: Map<NotificationType, NotificationHandler> by lazy {
        handlers.associateBy { it.notificationType }
    }

    fun getHandler(type: NotificationType): NotificationHandler? {
        return handlerMap[type]
    }

    fun hasHandler(type: NotificationType): Boolean {
        return handlerMap.containsKey(type)
    }
}

