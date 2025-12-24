package com.example.instagramclone.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagramclone.domain.model.Post
import com.example.instagramclone.domain.model.User
import com.example.instagramclone.domain.usecase.auth.GetCurrentUserUseCase
import com.example.instagramclone.domain.usecase.post.GetUserPostsUseCase
import com.example.instagramclone.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUserPostsUseCase: GetUserPostsUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _profile = MutableStateFlow<User?>(null)
    val profile: StateFlow<User?> = _profile.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    init {
        loadProfile()
        loadPosts()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                _profile.value = user
            }
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            try {
                val userPosts = getUserPostsUseCase()
                _posts.value = userPosts
            } catch (e: Exception) {
                // Handle error
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
}