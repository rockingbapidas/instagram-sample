package com.example.instagramclone.domain.model

sealed interface FeedItem {
    data class PostItem(val post: Post) : FeedItem
    data class AdItem(val ad: Ad) : FeedItem
}
