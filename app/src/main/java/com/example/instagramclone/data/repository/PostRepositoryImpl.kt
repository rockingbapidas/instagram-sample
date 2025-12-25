package com.example.instagramclone.data.repository

import android.net.Uri
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.instagramclone.data.local.entities.PostWithComments
import com.example.instagramclone.data.local.AppDatabase
import com.example.instagramclone.data.mapper.PostMapper
import com.example.instagramclone.data.mapper.toDomain
import com.example.instagramclone.data.mapper.toEntity
import com.example.instagramclone.data.remote.api.PostApi
import com.example.instagramclone.domain.model.Comment
import com.example.instagramclone.domain.model.Post
import com.example.instagramclone.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val api: PostApi,
    private val db: AppDatabase
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getPosts(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = PostRemoteMediator(api, db),
            pagingSourceFactory = { db.postDao().pagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { postWithComments ->
                PostMapper.toDomain(
                    postWithComments.post,
                    postWithComments.comments.map { it.toDomain() }
                )
            }
        }
    }

    override suspend fun getRecentPosts(): List<Post> {
        val response = api.getPosts(1, null)
        return response.items.map { dto ->
             val entity = PostMapper.toEntity(dto)
             PostMapper.toDomain(entity, emptyList())
        }
    }
    
    override suspend fun hasNewPosts(): Boolean {
         val latestLocal = db.postDao().getLatestPost()
         val remote = getRecentPosts()
         if (remote.isNotEmpty()) {
             // If local is empty, everything is new, but we might just be starting up.
             // If we have items, check ID.
             return latestLocal != null && remote.first().id != latestLocal.id
         }
         return false
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