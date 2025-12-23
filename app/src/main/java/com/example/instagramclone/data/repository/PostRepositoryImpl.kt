package com.example.instagramclone.data.repository

import android.net.Uri
import com.example.instagramclone.data.local.AppDatabase
import com.example.instagramclone.data.local.dao.PostDao
import com.example.instagramclone.data.mapper.PostMapper
import com.example.instagramclone.data.mapper.toDomain
import com.example.instagramclone.data.mapper.toEntity
import com.example.instagramclone.data.remote.api.InstagramApi
import com.example.instagramclone.domain.model.Post
import com.example.instagramclone.domain.model.Comment
import com.example.instagramclone.domain.model.Profile
import com.example.instagramclone.domain.repository.PostRepository
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val api: InstagramApi,
    private val db: AppDatabase,
    private val postDao: PostDao,
    private val postMapper: PostMapper
) : PostRepository {
    override suspend fun getPosts(page: Int): List<Post> {
        val remotePosts = api.getPosts(page)
        db.postDao().insertPosts(remotePosts.map { postMapper.toEntity(it) })
        return db.postDao().getAllPosts().map { postEntity ->
            val comments = db.commentDao().getCommentsForPost(postEntity.id).map { it.toDomain() }
            postMapper.toDomain(postEntity, comments)
        }
    }

    override suspend fun getPost(id: String): Post {
        val postEntity = db.postDao().getPostById(id)
        val comments = db.commentDao().getCommentsForPost(id).map { it.toDomain() }
        return postMapper.toDomain(postEntity, comments)
    }

    override suspend fun createPost(imageUri: Uri, caption: String): Post {
        val postDto = api.createPost(imageUri, caption)
        val postEntity = postMapper.toEntity(postDto)
        db.postDao().insertPosts(listOf(postEntity))
        return postMapper.toDomain(postEntity, emptyList())
    }

    override suspend fun likePost(postId: String) {
        api.likePost(postId)
    }

    override suspend fun unlikePost(postId: String) {
        api.unlikePost(postId)
    }

    override suspend fun getUserPosts(): List<Post> {
        return db.postDao().getAllPosts().map { postEntity ->
            val comments = db.commentDao().getCommentsForPost(postEntity.id).map { it.toDomain() }
            postMapper.toDomain(postEntity, comments)
        }
    }

    override suspend fun searchPosts(query: String): List<Post> {
        val remotePosts = api.searchPosts(query)
        db.postDao().insertPosts(remotePosts.map { postMapper.toEntity(it) })
        return db.postDao().getAllPosts().map { postEntity ->
            val comments = db.commentDao().getCommentsForPost(postEntity.id).map { it.toDomain() }
            postMapper.toDomain(postEntity, comments)
        }
    }

    override suspend fun getComments(postId: String): List<Comment> {
        val remoteComments = api.getComments(postId)
        db.commentDao().insertComments(remoteComments.map { it.toEntity() })
        return db.commentDao().getCommentsForPost(postId).map { it.toDomain() }
    }
} 