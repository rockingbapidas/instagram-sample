package com.example.instagramclone.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.instagramclone.domain.model.FeedItem
import com.example.instagramclone.domain.model.Post
import com.example.instagramclone.domain.model.User
import com.example.instagramclone.domain.prefetch.MediaPrefetch
import com.example.instagramclone.domain.usecase.auth.GetCurrentUserUseCase
import com.example.instagramclone.domain.usecase.post.GetUserPostsUseCase
import com.example.instagramclone.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUserPostsUseCase: GetUserPostsUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val mediaPrefetch: MediaPrefetch
) : ViewModel() {

    private val _profile = MutableStateFlow<User?>(null)
    val profile: StateFlow<User?> = _profile.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val posts: Flow<PagingData<FeedItem.PostItem>> = _profile
        .flatMapLatest { user ->
            if (user != null) {
                getUserPostsUseCase(user.id)
            } else {
                flowOf(PagingData.empty())
            }
        }.map { pagingData ->
            pagingData.map { post -> FeedItem.PostItem(post) }
        }.cachedIn(viewModelScope)

    init {
        syncData()
    }

    private fun syncData() {
        // Observe Profile
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                _profile.value = user
            }
        }

        // Trigger initial Profile refresh
        viewModelScope.launch {
            try {
                getCurrentUserUseCase.refresh()
            } catch (e: Exception) {
                // Ignore refresh error
            }
        }
    }

    fun editProfile() {
        // TODO: Implement edit profile navigation
    }

    fun logout() {
        viewModelScope.launch {
            try {
                logoutUseCase()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun onScrollChanged(items: List<FeedItem>, firstVisibleIndex: Int) {
        mediaPrefetch.onScrollChanged(items, firstVisibleIndex)
    }
}