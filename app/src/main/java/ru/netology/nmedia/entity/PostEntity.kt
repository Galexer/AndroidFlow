package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorId: Long = 0L,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val hidden: Boolean = false,
    @Embedded
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
) {
    fun toDto() = Post(
        id,
        author,
        authorId,
        authorAvatar,
        content,
        published,
        likedByMe,
        likes,
        attachment,
        ownedByMe
    )

    companion object {
        fun fromDto(dto: Post, hidden: Boolean) =
            PostEntity(
                dto.id,
                dto.author,
                dto.authorId,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.likedByMe,
                dto.likes,
                hidden = hidden,
                dto.attachment,
                dto.ownedByMe,
            )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(hidden: Boolean = false): List<PostEntity> =
    map { PostEntity.fromDto(it, hidden) }
