package com.example.instagramclone.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class PostWithComments(
    @Embedded
    val post: PostEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "postId"
    )
    val comments: List<CommentEntity>
)
