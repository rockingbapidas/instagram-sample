package com.example.instagramclone.data.remote.dto

data class FeedResponseDto(
    val items: List<PostDto>,
    val nextCursor: String?
)
