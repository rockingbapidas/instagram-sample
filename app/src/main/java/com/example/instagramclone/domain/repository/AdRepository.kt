package com.example.instagramclone.domain.repository

import com.example.instagramclone.domain.model.Ad

interface AdRepository {
    fun getAd(id: String): Ad
}