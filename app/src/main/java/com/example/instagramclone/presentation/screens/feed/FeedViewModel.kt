package com.example.instagramclone.presentation.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagramclone.domain.model.Post
import com.example.instagramclone.domain.usecase.post.GetPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentPage = 1
    private var hasMorePosts = true

    init {
        loadPosts()
    }

    fun loadPosts() {
        if (!hasMorePosts || _isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val newPosts = getPostsUseCase(currentPage)
                if (newPosts.isEmpty()) {
                    hasMorePosts = false
                } else {
                    _posts.value = if (currentPage == 1) newPosts else _posts.value + newPosts
                    currentPage++
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load posts"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        currentPage = 1
        hasMorePosts = true
        _posts.value = emptyList()
        loadPosts()
    }

    fun clearError() {
        _error.value = null
    }
}