package com.example.instagramclone.data.remote.api

import com.example.instagramclone.data.remote.dto.LoginRequest
import com.example.instagramclone.data.remote.dto.LoginResponse
import com.example.instagramclone.data.remote.dto.RegisterRequest
import com.example.instagramclone.data.remote.dto.RegisterResponse
import com.example.instagramclone.data.remote.dto.UserDto
import retrofit2.http.*

interface UserApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("auth/logout")
    suspend fun logout()

    @GET("auth/me")
    suspend fun getCurrentUser(): UserDto

    @GET("profile")
    suspend fun getProfile(): UserDto
}
