package com.example.instagramclone.data.repository

import android.net.Uri
import com.example.instagramclone.data.local.AppDatabase
import com.example.instagramclone.data.local.dao.PostDao
import com.example.instagramclone.data.mapper.PostMapper
import com.example.instagramclone.data.mapper.toDomain
import com.example.instagramclone.data.mapper.toEntity
import com.example.instagramclone.data.remote.api.PostApi
import com.example.instagramclone.domain.model.Post
import com.example.instagramclone.domain.model.Comment
import com.example.instagramclone.domain.model.FeedPage
import com.example.instagramclone.domain.repository.PostRepository
import com.example.instagramclone.data.local.preferences.FeedPreferences
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val api: PostApi,
    private val db: AppDatabase,
    private val feedPreferences: FeedPreferences
) : PostRepository {
    override suspend fun getCachedPosts(): FeedPage {
        val entities = db.postDao().getAllPosts()
        val posts = entities.map { postEntity ->
            val comments = db.commentDao().getCommentsForPost(postEntity.id).map { it.toDomain() }
            PostMapper.toDomain(postEntity, comments)
        }
        val cursor = feedPreferences.getNextCursor()
        return FeedPage(items = posts, nextCursor = cursor)
    }

    override suspend fun getPosts(page: Int, cursor: String?): FeedPage {
        val remoteResponse = api.getPosts(page, cursor)
        db.postDao().insertPosts(remoteResponse.items.map { PostMapper.toEntity(it) })

        feedPreferences.saveNextCursor(remoteResponse.nextCursor)
        
        val items = remoteResponse.items.map { dto ->
            val entity = PostMapper.toEntity(dto)
            // We just inserted/updated it, so we can use the entity data. 
            // We also need comments.
            val comments = db.commentDao().getCommentsForPost(entity.id).map { it.toDomain() }
            PostMapper.toDomain(entity, comments)
        }
        
        return FeedPage(
            items = items,
            nextCursor = remoteResponse.nextCursor
        )
    }

    override suspend fun getPost(id: String): Post {
        val postEntity = db.postDao().getPostById(id)
        val comments = db.commentDao().getCommentsForPost(id).map { it.toDomain() }
        return PostMapper.toDomain(postEntity, comments)
    }

    override suspend fun createPost(imageUri: Uri, caption: String): Post {
        val postDto = api.createPost(imageUri, caption)
        val postEntity = PostMapper.toEntity(postDto)
        db.postDao().insertPosts(listOf(postEntity))
        return PostMapper.toDomain(postEntity, emptyList())
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
            PostMapper.toDomain(postEntity, comments)
        }
    }

    override suspend fun searchPosts(query: String): List<Post> {
        val remotePosts = api.searchPosts(query)
        db.postDao().insertPosts(remotePosts.map { PostMapper.toEntity(it) })
        return db.postDao().getAllPosts().map { postEntity ->
            val comments = db.commentDao().getCommentsForPost(postEntity.id).map { it.toDomain() }
            PostMapper.toDomain(postEntity, comments)
        }
    }

    override suspend fun getComments(postId: String): List<Comment> {
        val remoteComments = api.getComments(postId)
        db.commentDao().insertComments(remoteComments.map { it.toEntity() })
        return db.commentDao().getCommentsForPost(postId).map { it.toDomain() }
    }
}