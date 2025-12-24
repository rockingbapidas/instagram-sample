package com.example.instagramclone.domain.usecase.post

import com.example.instagramclone.domain.repository.PostRepository
import javax.inject.Inject

class LikePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(postId: String) {
        repository.likePost(postId)
    }
}