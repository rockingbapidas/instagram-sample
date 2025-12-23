package com.example.instagramclone.domain.usecase

import com.example.instagramclone.domain.model.Profile
import com.example.instagramclone.domain.repository.PostRepository
import com.example.instagramclone.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Profile {
        return repository.getProfile()
    }
}