package com.example.instagramclone.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagramclone.domain.model.Post
import com.example.instagramclone.domain.model.Profile
import com.example.instagramclone.domain.usecase.GetProfileUseCase
import com.example.instagramclone.domain.usecase.GetUserPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val getUserPostsUseCase: GetUserPostsUseCase
) : ViewModel() {

    private val _profile = MutableStateFlow(
        Profile(
            username = "username",
            displayName = "Display Name",
            bio = "Bio goes here",
            profilePictureUrl = "https://example.com/profile.jpg",
            followers = 0,
            following = 0
        )
    )
    val profile: StateFlow<Profile> = _profile.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    init {
        loadProfile()
        loadPosts()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val profileData = getProfileUseCase()
                _profile.value = profileData
            } catch (e: Exception) {
                // Handle error
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
}