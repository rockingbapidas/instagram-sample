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

    data class FeedState(
        val items: List<Post> = emptyList(),
        val nextCursor: String? = null,
        val isLoading: Boolean = false,
        val endReached: Boolean = false,
        val error: String? = null,
        val hasNewPosts: Boolean = false
    )

    private val _state = MutableStateFlow(FeedState())
    val state: StateFlow<FeedState> = _state.asStateFlow()

    private val pendingPosts = mutableListOf<Post>()

    init {
        loadPosts()
        startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(30_000) // 30 seconds
                checkForNewPosts()
            }
        }
    }

    private suspend fun checkForNewPosts() {
        if (_state.value.isLoading) return // Skip if already loading main feed

        try {
            // Fetch first page (cursor = null)
            val result = getPostsUseCase(1, null)
            val fetchedItems = result.items
            
            val currentIds = _state.value.items.map { it.id }.toSet()
            val pendingIds = pendingPosts.map { it.id }.toSet()
            
            val newUniqueItems = fetchedItems.filter { 
                it.id !in currentIds && it.id !in pendingIds 
            }

            if (newUniqueItems.isNotEmpty()) {
                pendingPosts.addAll(0, newUniqueItems) // Add to top of pending
                _state.value = _state.value.copy(hasNewPosts = true)
            }
        } catch (e: Exception) {
            // Silent failure for polling
        }
    }

    fun showNewPosts() {
        val currentItems = _state.value.items
        // pendingPosts are already separate from currentItems. 
        // We put pending at top.
        // pendingPosts might have duplicates within itself if multiple polls happened?
        // My logic `it.id !in pendingIds` prevents that.
        // But `addAll(0, newUniqueItems)` puts newest poll results at TOP of pending.
        // So pending is ordered: [Newest Poll Items, Older Poll Items, ..., Oldest Poll Items]
        // This is correct order for feed.
        
        val newItemsList = pendingPosts + currentItems
        
        _state.value = _state.value.copy(
            items = newItemsList,
            hasNewPosts = false
        )
        pendingPosts.clear()
    }

    fun loadPosts() {
        if (_state.value.isLoading || _state.value.endReached) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val currentCursor = _state.value.nextCursor
                
                val result = getPostsUseCase(1, currentCursor) 
                
                val newItems = result.items
                val newCursor = result.nextCursor
                
                if (newItems.isEmpty() && newCursor == null) {
                   _state.value = _state.value.copy(
                       isLoading = false,
                       endReached = true
                   )
                } else {
                    val currentItems = _state.value.items
                    val combinedItems = (currentItems + newItems).distinctBy { it.id }
                    
                    _state.value = _state.value.copy(
                        isLoading = false,
                        items = combinedItems,
                        nextCursor = newCursor,
                        endReached = newCursor == null
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load posts"
                )
            }
        }
    }

    fun refresh() {
        _state.value = FeedState(isLoading = true) 
        pendingPosts.clear() // Clear pending on manual refresh
        viewModelScope.launch {
            try {
                val result = getPostsUseCase(1, null)
                _state.value = _state.value.copy(
                    isLoading = false,
                    items = result.items,
                    nextCursor = result.nextCursor,
                    endReached = result.nextCursor == null,
                    hasNewPosts = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load posts"
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}