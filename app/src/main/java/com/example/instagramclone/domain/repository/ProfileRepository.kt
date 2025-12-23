package com.example.instagramclone.domain.repository

import com.example.instagramclone.domain.model.Profile

interface ProfileRepository {
    suspend fun getProfile(): Profile
}