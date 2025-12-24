package com.example.instagramclone.domain.model

data class User(
    val id: String,
    val username: String,
    val email: String,
    val displayName: String,
    val bio: String = "",
    val profilePictureUrl: String = "",
    val followers: Int = 0,
    val following: Int = 0
)
