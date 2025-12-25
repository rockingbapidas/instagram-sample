package com.example.instagramclone.data.repository

import com.example.instagramclone.domain.model.Ad
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdRepository @Inject constructor() {
    
    fun getAd(id: String): Ad {
        return Ad(
            id = id,
            title = "Sponsored Content $id",
            content = "Check out this amazing product!",
            imageUrl = "https://picsum.photos/seed/$id/400/300"
        )
    }
}
