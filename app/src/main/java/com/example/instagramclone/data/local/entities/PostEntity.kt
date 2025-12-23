package com.example.instagramclone.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val username: String,
    val imageUrl: String,
    val caption: String,
    val likes: Int,
    val timestamp: Long
) 