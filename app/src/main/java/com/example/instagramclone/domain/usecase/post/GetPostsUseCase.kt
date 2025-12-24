package com.example.instagramclone.domain.usecase.post

import com.example.instagramclone.domain.model.Post
import com.example.instagramclone.domain.repository.PostRepository
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(page: Int): List<Post> {
        return repository.getPosts(page)
    }
}