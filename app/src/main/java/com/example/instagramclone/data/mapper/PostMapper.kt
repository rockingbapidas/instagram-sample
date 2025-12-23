package com.example.instagramclone.data.mapper

import com.example.instagramclone.data.local.entities.PostEntity
import com.example.instagramclone.data.remote.dto.PostDto
import com.example.instagramclone.domain.model.Post
import com.example.instagramclone.domain.model.Comment

object PostMapper {
    fun toEntity(dto: PostDto): PostEntity =
        PostEntity(dto.id, dto.username, dto.imageUrl, dto.caption, dto.likes, dto.timestamp)

    fun toDomain(entity: PostEntity, comments: List<Comment>): Post =
        Post(entity.id, entity.username, entity.imageUrl, entity.caption, entity.likes, comments, entity.timestamp)

    fun toEntity(domain: Post): PostEntity =
        PostEntity(domain.id, domain.username, domain.imageUrl, domain.caption, domain.likes, domain.timestamp)
} 