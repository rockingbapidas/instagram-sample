package com.example.instagramclone.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("bio")
    val bio: String? = null,
    @SerializedName("profile_picture_url")
    val profilePictureUrl: String? = null,
    @SerializedName("followers")
    val followers: Int? = null,
    @SerializedName("following")
    val following: Int? = null,
    @SerializedName("post_count")
    val postCount: Int? = null
)

data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class LoginResponse(
    @SerializedName("user")
    val user: UserDto,
    @SerializedName("token")
    val token: String
)

data class RegisterRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class RegisterResponse(
    @SerializedName("user")
    val user: UserDto,
    @SerializedName("token")
    val token: String
)
