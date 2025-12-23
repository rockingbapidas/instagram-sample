package com.example.instagramclone.domain.repository

import android.net.Uri
import com.example.instagramclone.domain.model.Post
import com.example.instagramclone.domain.model.Comment

interface PostRepository {
    suspend fun getPosts(page: Int): List<Post>
    suspend fun getPost(id: String): Post
    suspend fun createPost(imageUri: Uri, caption: String): Post
    suspend fun likePost(postId: String)
    suspend fun unlikePost(postId: String)
    suspend fun getUserPosts(): List<Post>
    suspend fun searchPosts(query: String): List<Post>
    suspend fun getComments(postId: String): List<Comment>
} 