package com.example.instagramclone.domain.usecase.post

import android.net.Uri
import com.example.instagramclone.domain.model.Post
import com.example.instagramclone.domain.repository.PostRepository
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(imageUri: Uri, caption: String): Post {
        return repository.createPost(imageUri, caption)
    }
}