package com.example.instagramclone.data.remote.api

import android.net.Uri
import com.example.instagramclone.data.remote.dto.PostDto
import com.example.instagramclone.data.remote.dto.CommentDto
import com.example.instagramclone.domain.model.Profile
import retrofit2.http.*

interface InstagramApi {
    @GET("posts")
    suspend fun getPosts(@Query("page") page: Int): List<PostDto>

    @GET("posts/{id}")
    suspend fun getPost(@Path("id") id: String): PostDto

    @POST("posts")
    suspend fun createPost(
        @Query("imageUri") imageUri: Uri,
        @Query("caption") caption: String
    ): PostDto

    @POST("posts/{id}/like")
    suspend fun likePost(@Path("id") id: String)

    @DELETE("posts/{id}/like")
    suspend fun unlikePost(@Path("id") id: String)

    @GET("profile")
    suspend fun getProfile(): Profile

    @GET("profile/posts")
    suspend fun getUserPosts(): List<PostDto>

    @GET("search")
    suspend fun searchPosts(@Query("query") query: String): List<PostDto>

    @GET("posts/{id}/comments")
    suspend fun getComments(@Path("id") id: String): List<CommentDto>
} 