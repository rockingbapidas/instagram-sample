package com.example.instagramclone.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.instagramclone.data.local.dao.PostDao
import com.example.instagramclone.data.local.dao.CommentDao
import com.example.instagramclone.data.local.dao.NotificationDao
import com.example.instagramclone.data.local.entities.PostEntity
import com.example.instagramclone.data.local.entities.CommentEntity
import com.example.instagramclone.data.local.entities.NotificationEntity
import com.example.instagramclone.data.local.converters.NotificationTypeConverter

@Database(
    entities = [PostEntity::class, CommentEntity::class, NotificationEntity::class],
    version = 2
)
@TypeConverters(NotificationTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun notificationDao(): NotificationDao
} 