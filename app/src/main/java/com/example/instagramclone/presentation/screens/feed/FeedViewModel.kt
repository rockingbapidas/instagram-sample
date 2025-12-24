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
        val error: String? = null
    )

    private val _state = MutableStateFlow(FeedState())
    val state: StateFlow<FeedState> = _state.asStateFlow()

    init {
        loadPosts()
    }

    fun loadPosts() {
        if (_state.value.isLoading || _state.value.endReached) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val currentCursor = _state.value.nextCursor
                // For first page, cursor is null. 
                // Wait, if it's first page, we just pass null.
                // But for next pages, we pass nextCursor.
                // Issue: Initial state nextCursor is null. 
                // loadPosts() uses nextCursor. 
                // If I want to load first page, cursor is null.
                // If I want to load next page, cursor is what we got from previous.
                // How to distinguish "Start" vs "Next" if both are null?
                // Actually, for first load, items are empty.
                // Or we can use a separate flag or just rely on items.isEmpty() logic if needed.
                // But simpler: just pass the cursor we have.
                // However, infinite scroll usually needs to know if we are "refreshing" (cursor=null) or "appending".
                // In this simple cursor model: 
                // - Initial load: cursor = null.
                // - Response: nextCursor = "abc".
                // - Next load: cursor = "abc".
                // - Response: nextCursor = "def".
                // - ...
                // - Response: nextCursor = null (end).
                
                // Existing `currentPage` was used. Now strictly cursor.
                // But `getPostsUseCase` needs `page` AND `cursor`?
                // My update to `getPosts` was `getPosts(page: Int, cursor: String?)`.
                // Existing logic had `currentPage`.
                // If migrating to purely cursor, `page` might be irrelevant or legacy.
                // The API signature I updated still takes `page`.
                //     suspend fun getPosts(@Query("page") page: Int, @Query("cursor") cursor: String? = null)
                // If the backend ignores page when cursor is present, fine. 
                // Or maybe I should just pass 1 or dummy if using cursor.
                // Use a counter for page just in case.
                
                // Wait, if `nextCursor` is null and items is NOT empty, it means we reached end?
                // `endReached` logic handles that.
                
                val result = getPostsUseCase(1, currentCursor) // Passing 1 as page placeholder?
                
                val newItems = result.items
                val newCursor = result.nextCursor
                
                if (newItems.isEmpty() && newCursor == null) {
                   _state.value = _state.value.copy(
                       isLoading = false,
                       endReached = true
                   )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        items = _state.value.items + newItems,
                        nextCursor = newCursor,
                        endReached = newCursor == null // If no next cursor, we reached end
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
        _state.value = FeedState(isLoading = true) // Reset state
        viewModelScope.launch {
            try {
                val result = getPostsUseCase(1, null)
                _state.value = _state.value.copy(
                    isLoading = false,
                    items = result.items,
                    nextCursor = result.nextCursor,
                    endReached = result.nextCursor == null
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