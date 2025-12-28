package com.example.instagramclone.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.instagramclone.data.local.AppDatabase
import com.example.instagramclone.data.local.entities.PostWithComments
import com.example.instagramclone.data.local.entities.RemoteKeysEntity
import com.example.instagramclone.data.mapper.PostMapper
import com.example.instagramclone.data.remote.api.PostApi
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val api: PostApi,
    private val db: AppDatabase,
    private val userId: String? = null
) : RemoteMediator<Int, PostWithComments>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostWithComments>
    ): MediatorResult {
        val cursor = when (loadType) {
            LoadType.REFRESH -> {
                null
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }

        try {
            val page = 1 // Cursor-based API usually ignores this or uses it as start
            val apiResponse = if (userId != null) {
                api.getUserPosts(userId, page, cursor)
            } else {
                api.getPosts(page, cursor)
            }

            val posts = apiResponse.items
            val endOfPaginationReached = apiResponse.nextCursor == null

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    if (userId != null) {
                        // If it's for a specific user, we might only want to clear that user's posts
                        db.remoteKeysDao().clearRemoteKeys() // For simplicity, clearing all keys
                        db.postDao().deletePostsByUserId(userId)
                    } else {
                        db.remoteKeysDao().clearRemoteKeys()
                        db.postDao().deleteAllPosts()
                    }
                }

                val keys = posts.map { post ->
                    RemoteKeysEntity(
                        id = post.id,
                        prevKey = cursor,
                        nextKey = apiResponse.nextCursor
                    )
                }

                db.remoteKeysDao().insertAll(keys)
                db.postDao().insertPosts(posts.map { 
                    PostMapper.toEntity(it).copy(userId = userId) 
                })
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PostWithComments>): RemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { postWithComments ->
                db.remoteKeysDao().remoteKeysPostId(postWithComments.post.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, PostWithComments>): RemoteKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { postWithComments ->
                db.remoteKeysDao().remoteKeysPostId(postWithComments.post.id)
            }
    }
}
