package com.example.instagramclone.domain.usecase.auth

import com.example.instagramclone.domain.model.User
import com.example.instagramclone.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> {
        return authRepository.getCurrentUser()
    }

    suspend fun refresh() {
        authRepository.refreshCurrentUser()
    }
}
