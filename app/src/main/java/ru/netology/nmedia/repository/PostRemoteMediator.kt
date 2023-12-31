package ru.netology.nmedia.repository

import androidx.paging.*
import androidx.room.withTransaction
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val postDao: PostDao,
    private val service: ApiService,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb,
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    if (postRemoteKeyDao.max() != null) {
                        postRemoteKeyDao.max()?.let {
                            service.getAfter(it, state.config.pageSize)
                        }
                    } else {
                        service.getLatest(state.config.pageSize)
                    }
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(false)
                }
                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    service.getBefore(id, state.config.pageSize)
                }
            }

            if ((response != null) && !response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response!!.body() ?: throw ApiError(
                response.code(),
                response.message(),
            )
            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        postRemoteKeyDao.insert(
                            if (postRemoteKeyDao.isEmpty()) {
                                listOf(
                                    PostRemoteKeyEntity(
                                        PostRemoteKeyEntity.keyType.AFTER,
                                        body.first().id
                                    ),
                                    PostRemoteKeyEntity(
                                        PostRemoteKeyEntity.keyType.BEFORE,
                                        body.last().id
                                    )
                                )
                            } else {
                                listOf(
                                    PostRemoteKeyEntity(
                                        PostRemoteKeyEntity.keyType.AFTER,
                                        body.first().id
                                    )
                                )
                            }
                        )
                    }
                    LoadType.PREPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.keyType.AFTER, body.first().id
                            )
                        )
                    }
                    LoadType.APPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.keyType.BEFORE, body.last().id
                            )
                        )
                    }
                }
                postDao.insert(body.toEntity())
            }

            return MediatorResult.Success(body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}