package com.example.instagramclone.data.local.dao

import androidx.room.*
import com.example.instagramclone.data.local.entities.CommentEntity

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    suspend fun getCommentsForPost(postId: String): List<CommentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)
} 