package com.example.instagramclone.data.local.converters

import androidx.room.TypeConverter
import com.example.instagramclone.domain.model.NotificationType

class NotificationTypeConverter {
    @TypeConverter
    fun fromNotificationType(type: NotificationType): String {
        return type.name
    }

    @TypeConverter
    fun toNotificationType(type: String): NotificationType {
        return NotificationType.valueOf(type)
    }
}

