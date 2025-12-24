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
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val api: PostApi,
    private val db: AppDatabase
) : PostRepository {
    override suspend fun getPosts(page: Int, cursor: String?): FeedPage {
        val remoteResponse = api.getPosts(page, cursor)
        db.postDao().insertPosts(remoteResponse.items.map { PostMapper.toEntity(it) })
        
        // Caching strategy: We save to DB, but for the return value we use the remote response
        // to ensure we respect the cursor/pagination for the UI state.
        // To populate full domain objects (with comments), we might need to query DB or map DTOs directly.
        // Existing mapper converts Entity -> Domain. I probably need DTO -> Domain mapper too or 
        // rely on the Entity -> Domain conversion for the items we just inserted.
        // Let's use the inserted entities to get domain objects to be consistent with existing flow
        // but detailed implementation might vary.
        // Simpler approach for now: Map DTOs to Entities, then match those Entities to Domain.
        // Or simply: Map remoteResponse.items to Domain (missing comments if DTO doesn't have them).
        // PostDto has no comments. PostEntity has no comments. Comments are fetched separately in existing code?
        // Ah, `db.commentDao().getCommentsForPost` is used.
        // Let's stick to the pattern: Insert, then fetch relevant items? 
        // Fetching "relevant items" from DB without a list of IDs is hard.
        // Better: Map remote response items to domain objects, fetching comments for each if needed (or empty).
        // Existing `getPosts` returned `db.postDao().getAllPosts()`.
        // I will change it to return the mapped data from the API response to correct the pagination behavior,
        // while also caching it.
        
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