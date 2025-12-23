package com.example.instagramclone.data.mapper

import com.example.instagramclone.data.local.entities.CommentEntity
import com.example.instagramclone.data.remote.dto.CommentDto
import com.example.instagramclone.domain.model.Comment

fun CommentDto.toEntity() = CommentEntity(id, postId, username, text, timestamp)
fun CommentEntity.toDomain() = Comment(id, username, text, timestamp)
fun Comment.toEntity(postId: String) = CommentEntity(id, postId, username, text, timestamp) 