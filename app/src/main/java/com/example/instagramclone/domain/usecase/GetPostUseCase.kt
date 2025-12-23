package com.example.instagramclone.domain.usecase

import com.example.instagramclone.domain.model.Post
import com.example.instagramclone.domain.repository.PostRepository
import javax.inject.Inject

class GetPostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(id: String): Post {
        return repository.getPost(id)
    }
}