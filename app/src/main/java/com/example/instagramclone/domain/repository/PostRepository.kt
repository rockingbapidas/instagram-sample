package com.example.instagramclone.domain.repository

import android.net.Uri
import androidx.paging.PagingData
import com.example.instagramclone.domain.model.Comment
import com.example.instagramclone.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getPosts(): Flow<PagingData<Post>>
    suspend fun getRecentPosts(): List<Post>
    suspend fun hasNewPosts(): Boolean
    suspend fun getPost(id: String): Post
    suspend fun createPost(imageUri: Uri, caption: String): Post
    suspend fun likePost(postId: String)
    suspend fun unlikePost(postId: String)
    fun getUserPosts(userId: String): Flow<List<Post>>
    suspend fun refreshUserPosts(userId: String)
    suspend fun searchPosts(query: String): List<Post>
    suspend fun getComments(postId: String): List<Comment>
} 