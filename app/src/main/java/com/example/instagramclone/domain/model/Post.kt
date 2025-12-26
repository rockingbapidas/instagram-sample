package com.example.instagramclone.domain.model

data class Post(
    val id: String,
    val username: String,
    val imageUrl: String,
    val caption: String,
    val likes: Int,
    val comments: List<Comment>,
    val timestamp: Long
)

data class Comment(
    val id: String,
    val username: String,
    val text: String,
    val timestamp: Long
)