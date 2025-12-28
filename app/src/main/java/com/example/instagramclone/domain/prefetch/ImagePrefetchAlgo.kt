package com.example.instagramclone.domain.prefetch

import android.content.Context
import androidx.annotation.MainThread
import coil.ImageLoader
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.Disposable
import coil.request.ImageRequest
import coil.size.Size
import com.example.instagramclone.domain.model.FeedItem
import com.example.instagramclone.domain.model.Post
import javax.inject.Inject
import kotlin.math.abs

class ImagePrefetchAlgo @Inject constructor(
    private val context: Context,
    private val performanceMonitor: PerformanceMonitor
): MediaPrefetch {
    private val imageLoader: ImageLoader = context.imageLoader
    private val activeRequests = mutableMapOf<String, Disposable>()
    
    // Tracks the last reported scroll position to calculate direction and velocity
    private var lastVisibleIndex = -1
    private var lastScrollTime = 0L

    /**
     * Called when the scroll position changes.
     * @param items The list of currently visible feed items.
     * @param firstVisibleIndex The index of the first visible item.
     */
    @MainThread
    override fun onScrollChanged(items: List<FeedItem>, firstVisibleIndex: Int) {
        if (!performanceMonitor.isPrefetchEnabled()) {
            cancelAll()
            return
        }

        val currentTime = System.currentTimeMillis()
        val deltaTime = currentTime - lastScrollTime
        val deltaIndex = firstVisibleIndex - lastVisibleIndex
        
        // Velocity in items per second (rough approximation)
        val velocity = if (deltaTime > 0) abs(deltaIndex.toFloat() / deltaTime * 1000) else 0f
        val direction = if (deltaIndex >= 0) 1 else -1 // 1 for down, -1 for up

        // Fast scroll -> cancel prefetch to save bandwidth/CPU for visible items
        if (velocity > 10) { 
            cancelAll()
            lastVisibleIndex = firstVisibleIndex
            lastScrollTime = currentTime
            return
        }

        // Reverse scroll -> cancel previous prefetch tasks
        val isReverseScroll = direction != (if (firstVisibleIndex >= lastVisibleIndex) 1 else -1)
        if (lastVisibleIndex != -1 && isReverseScroll) {
            cancelAll()
        }

        lastVisibleIndex = firstVisibleIndex
        lastScrollTime = currentTime

        // Adaptive prefetch count based on network
        val networkType = performanceMonitor.getNetworkType()
        val prefetchCount = when (networkType) {
            NetworkType.WIFI -> 5
            NetworkType.CELLULAR_4G -> 3
            NetworkType.CELLULAR_3G -> 1
            else -> 0
        }

        // Prefetch upcoming items
        for (i in 1..prefetchCount) {
            val targetIndex = firstVisibleIndex + (i * direction) + (if (direction == 1) 2 else 0) // Look ahead based on direction
            if (targetIndex in items.indices) {
                val item = items[targetIndex]
                if (item is FeedItem.PostItem) {
                    prefetchPost(item.post, distance = i, networkType = networkType)
                }
            }
        }
    }

    private fun prefetchPost(post: Post, distance: Int, networkType: NetworkType) {
        val url = post.imageUrl
        if (activeRequests.containsKey(url)) return

        val requestBuilder = ImageRequest.Builder(context)
            .data(url)
            
        // Network-aware quality selection
        when (networkType) {
            NetworkType.WIFI -> {
                // High quality, both caches
                requestBuilder.size(Size.ORIGINAL)
            }
            NetworkType.CELLULAR_4G -> {
                // Medium quality for distance > 1
                if (distance > 1) {
                    requestBuilder.size(500, 500)
                }
            }
            NetworkType.CELLULAR_3G -> {
                // Low quality only
                requestBuilder.size(200, 200)
            }
            else -> {}
        }

        // Distance-aware cache policy
        // If close to screen (< 2 items away), prefetch to memory too
        if (distance <= 2) {
            requestBuilder.memoryCachePolicy(CachePolicy.ENABLED)
        } else {
            requestBuilder.memoryCachePolicy(CachePolicy.DISABLED) // Disk only
        }

        val request = requestBuilder.build()
        activeRequests[url] = imageLoader.enqueue(request)
    }

    override fun cancelAll() {
        activeRequests.values.forEach { it.dispose() }
        activeRequests.clear()
    }
}
