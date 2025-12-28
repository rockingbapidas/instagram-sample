package com.example.instagramclone.data.remote.api

import android.net.Uri
import com.example.instagramclone.data.remote.dto.CommentDto
import com.example.instagramclone.data.remote.dto.PostDto
import com.example.instagramclone.data.remote.dto.FeedResponseDto
import retrofit2.http.*

interface PostApi {
    @GET("posts")
    suspend fun getPosts(
        @Query("page") page: Int,
        @Query("cursor") cursor: String? = null
    ): FeedResponseDto

    @GET("users/{userId}/posts")
    suspend fun getUserPosts(
        @Path("userId") userId: String,
        @Query("page") page: Int,
        @Query("cursor") cursor: String? = null
    ): FeedResponseDto

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

    @GET("search")
    suspend fun searchPosts(@Query("query") query: String): List<PostDto>

    @GET("posts/{id}/comments")
    suspend fun getComments(@Path("id") id: String): List<CommentDto>
}
