package com.example.instagramclone.domain.usecase.auth

import com.example.instagramclone.domain.model.User
import com.example.instagramclone.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return authRepository.login(email, password)
    }
}
