package com.example.instagramclone.domain.model

data class Profile(
    val username: String,
    val displayName: String,
    val bio: String,
    val profilePictureUrl: String,
    val followers: Int,
    val following: Int
) 