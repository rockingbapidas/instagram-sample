package com.example.instagramclone.domain.prefetch

import androidx.annotation.MainThread
import com.example.instagramclone.domain.model.FeedItem

interface MediaPrefetch {
    @MainThread
    fun onScrollChanged(items: List<FeedItem>, firstVisibleIndex: Int)

    fun cancelAll()
}