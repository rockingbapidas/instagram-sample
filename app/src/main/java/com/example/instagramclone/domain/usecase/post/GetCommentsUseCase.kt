package com.example.instagramclone.domain.usecase.post

import com.example.instagramclone.domain.model.Comment
import com.example.instagramclone.domain.repository.PostRepository
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(postId: String): List<Comment> {
        return repository.getComments(postId)
    }
}