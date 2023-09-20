package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PostRemoteKeyEntity(
    @PrimaryKey
    val type: keyType,
    val key: Long,
) {
    enum class keyType {
        AFTER,
        BEFORE,
    }
}