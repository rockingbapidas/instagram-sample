package com.example.instagramclone.presentation.screens.create

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagramclone.domain.usecase.post.CreatePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase
) : ViewModel() {

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _isCreating = MutableStateFlow(false)
    val isCreating: StateFlow<Boolean> = _isCreating.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun setSelectedImage(uri: Uri) {
        _selectedImageUri.value = uri
    }

    fun createPost(caption: String) {
        val imageUri = _selectedImageUri.value ?: return
        
        viewModelScope.launch {
            _isCreating.value = true
            _error.value = null
            
            try {
                createPostUseCase(imageUri, caption)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to create post"
            } finally {
                _isCreating.value = false
            }
        }
    }
}