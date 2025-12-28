package com.example.instagramclone.data.repository

import com.example.instagramclone.domain.model.Ad
import com.example.instagramclone.domain.repository.AdRepository
import javax.inject.Inject
import javax.inject.Singleton

class AdRepositoryImpl @Inject constructor(): AdRepository {
    
    override fun getAd(id: String): Ad {
        return Ad(
            id = id,
            title = "Sponsored Content $id",
            content = "Check out this amazing product!",
            imageUrl = "https://picsum.photos/seed/$id/400/300"
        )
    }
}
