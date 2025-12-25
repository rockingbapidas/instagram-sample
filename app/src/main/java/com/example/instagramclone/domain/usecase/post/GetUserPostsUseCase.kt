package com.example.instagramclone.domain.usecase.post

import com.example.instagramclone.domain.model.Post
import com.example.instagramclone.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    operator fun invoke(userId: String): Flow<List<Post>> {
        return repository.getUserPosts(userId)
    }

    suspend fun refresh(userId: String) {
        repository.refreshUserPosts(userId)
    }
}