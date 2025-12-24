package com.example.instagramclone.domain.model

data class FeedPage(
    val items: List<Post>,
    val nextCursor: String?
)
