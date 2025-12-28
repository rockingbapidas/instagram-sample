package com.example.instagramclone.presentation.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.instagramclone.domain.model.FeedItem
import com.example.instagramclone.domain.prefetch.MediaPrefetch
import com.example.instagramclone.domain.repository.AdRepository
import com.example.instagramclone.domain.usecase.post.GetPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase,
    private val adRepository: AdRepository,
    private val mediaPrefetch: MediaPrefetch
) : ViewModel() {

    private val _hasNewPosts = MutableStateFlow(false)
    val hasNewPosts: StateFlow<Boolean> = _hasNewPosts.asStateFlow()

    val feedPagingFlow: Flow<PagingData<FeedItem>> = getPostsUseCase()
        .map { pagingData ->
            pagingData.map { post -> FeedItem.PostItem(post) }
                .insertSeparators { before: FeedItem.PostItem?, after: FeedItem.PostItem? ->
                    feedItemAdItem(before, after)
                }
        }
        .cachedIn(viewModelScope)

    init {
        startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                delay(30_000) // 30 seconds
                checkForNewPosts()
            }
        }
    }

    private suspend fun checkForNewPosts() {
        try {
            val hasNew = getPostsUseCase.hasNewPosts()
            if (hasNew) {
                _hasNewPosts.value = true
            }
        } catch (e: Exception) { 
            // Silent failure
        }
    }

    private fun feedItemAdItem(
        before: FeedItem.PostItem?,
        after: FeedItem.PostItem?
    ): FeedItem.AdItem? = if (before != null && after != null) {
        if (before.post.id.hashCode() % 5 == 0) {
            val ad = adRepository.getAd("ad_${before.post.id}")
            FeedItem.AdItem(ad)
        } else {
            null
        }
    } else {
        null
    }

    fun onNewPostsShown() {
        _hasNewPosts.value = false
    }

    fun onScrollChanged(items: List<FeedItem>, firstVisibleIndex: Int) {
        mediaPrefetch.onScrollChanged(items, firstVisibleIndex)
    }
}