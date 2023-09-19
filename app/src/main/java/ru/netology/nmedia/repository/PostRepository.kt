package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.model.PhotoModel

interface PostRepository {
    val data: Flow<PagingData<Post>>
    suspend fun getAll()
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, photo: PhotoModel)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun showAll()
    suspend fun getToken(login: String, pass: String): Token
    suspend fun registration(login: String, pass: String, name: String): Token
    suspend fun registrationWithPhoto(
        login: String,
        pass: String,
        name: String,
        photo: PhotoModel
    ): Token
}

