package com.example.instagramclone.presentation.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.instagramclone.domain.usecase.post.GetPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase
) : ViewModel() {

    private val _hasNewPosts = MutableStateFlow(false)
    val hasNewPosts: StateFlow<Boolean> = _hasNewPosts.asStateFlow()

    val feedPagingFlow = getPostsUseCase()
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

    fun onNewPostsShown() {
        _hasNewPosts.value = false
    }
}