package com.example.instagramclone.domain.repository

import com.example.instagramclone.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(username: String, email: String, password: String): Result<User>
    suspend fun logout()
    fun getCurrentUser(): Flow<User?>
    fun isAuthenticated(): Flow<Boolean>
    fun getAuthToken(): String?
}
