package com.example.instagramclone.data.repository

import com.example.instagramclone.data.remote.api.InstagramApi
import com.example.instagramclone.domain.model.Profile
import com.example.instagramclone.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: InstagramApi,
) : ProfileRepository {
    override suspend fun getProfile(): Profile {
        return api.getProfile()
    }
}