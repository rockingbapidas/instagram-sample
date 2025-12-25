package com.example.instagramclone.data.repository

import com.example.instagramclone.data.local.dao.UserDao
import com.example.instagramclone.data.local.preferences.AuthPreferences
import com.example.instagramclone.data.mapper.UserMapper
import com.example.instagramclone.data.remote.api.UserApi
import com.example.instagramclone.domain.model.User
import com.example.instagramclone.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: UserApi,
    private val authPreferences: AuthPreferences,
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = api.login(
                com.example.instagramclone.data.remote.dto.LoginRequest(email, password)
            )
            
            // Save token and user ID
            authPreferences.saveAuthToken(response.token)
            authPreferences.saveUserId(response.user.id)
            
            // Save user to local database
            val userEntity = UserMapper.toEntity(response.user)
            userDao.insertUser(userEntity)
            
            val user = UserMapper.toDomain(response.user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): Result<User> {
        return try {
            val response = api.register(
                com.example.instagramclone.data.remote.dto.RegisterRequest(
                    username,
                    email,
                    password
                )
            )
            
            // Save token and user ID
            authPreferences.saveAuthToken(response.token)
            authPreferences.saveUserId(response.user.id)
            
            // Save user to local database
            val userEntity = UserMapper.toEntity(response.user)
            userDao.insertUser(userEntity)
            
            val user = UserMapper.toDomain(response.user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        try {
            api.logout()
        } catch (e: Exception) {
            // Continue with local logout even if API call fails
        } finally {
            authPreferences.clearAuthData()
            userDao.deleteAllUsers()
        }
    }

    override suspend fun refreshCurrentUser() {
        try {
            val userDto = api.getCurrentUser()
            val userEntity = UserMapper.toEntity(userDto)
            userDao.insertUser(userEntity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getCurrentUser(): Flow<User?> {
        return userDao.getCurrentUser().map { userEntity ->
            userEntity?.let { UserMapper.toDomain(it) }
        }
    }

    override fun isAuthenticated(): Flow<Boolean> {
        return userDao.getCurrentUser().map { it != null }
    }

    override fun getAuthToken(): String? {
        return authPreferences.getAuthToken()
    }
}
