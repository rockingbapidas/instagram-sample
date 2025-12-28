package com.example.instagramclone.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val email: String,
    val displayName: String,
    val bio: String,
    val profilePictureUrl: String,
    val followers: Int,
    val following: Int,
    val postCount: Int
)
