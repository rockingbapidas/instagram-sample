package com.example.instagramclone.data.remote.dto

data class CommentDto(
    val id: String,
    val postId: String,
    val username: String,
    val text: String,
    val timestamp: Long
) 