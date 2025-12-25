package com.example.instagramclone.domain.usecase.post

import androidx.paging.PagingData
import com.example.instagramclone.domain.model.Post
import com.example.instagramclone.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    operator fun invoke(): Flow<PagingData<Post>> {
        return repository.getPosts()
    }
    
    suspend fun hasNewPosts(): Boolean {
        return repository.hasNewPosts()
    }
}