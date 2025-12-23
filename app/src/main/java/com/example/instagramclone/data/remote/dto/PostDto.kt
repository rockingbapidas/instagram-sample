package com.example.instagramclone.data.remote.dto

data class PostDto(
    val id: String,
    val username: String,
    val imageUrl: String,
    val caption: String,
    val likes: Int,
    val timestamp: Long
) 