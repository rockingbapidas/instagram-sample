package com.example.instagramclone.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.instagramclone.data.local.entities.PostEntity
import com.example.instagramclone.data.local.entities.PostWithComments
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    suspend fun getAllPosts(): List<PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Query("SELECT * FROM posts WHERE id = :id")
    suspend fun getPostById(id: String): PostEntity
    
    @Transaction
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun pagingSource(): PagingSource<Int, PostWithComments>

    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()

    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY timestamp DESC")
    fun getPostsByUserId(userId: String): Flow<List<PostWithComments>>

    @Query("SELECT * FROM posts ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestPost(): PostEntity?
} 