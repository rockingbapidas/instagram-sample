package com.example.instagramclone.data.mapper

import com.example.instagramclone.data.local.entities.UserEntity
import com.example.instagramclone.data.remote.dto.UserDto
import com.example.instagramclone.domain.model.User
import javax.inject.Inject

object UserMapper {
    fun toDomain(dto: UserDto): User {
        return User(
            id = dto.id,
            username = dto.username,
            email = dto.email,
            displayName = dto.displayName,
            bio = dto.bio ?: "",
            profilePictureUrl = dto.profilePictureUrl ?: "",
            followers = dto.followers ?: 0,
            following = dto.following ?: 0
        )
    }

    fun toDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            username = entity.username,
            email = entity.email,
            displayName = entity.displayName,
            bio = entity.bio,
            profilePictureUrl = entity.profilePictureUrl,
            followers = entity.followers,
            following = entity.following
        )
    }

    fun toEntity(user: User): UserEntity {
        return UserEntity(
            id = user.id,
            username = user.username,
            email = user.email,
            displayName = user.displayName,
            bio = user.bio,
            profilePictureUrl = user.profilePictureUrl,
            followers = user.followers,
            following = user.following
        )
    }

    fun toEntity(dto: UserDto): UserEntity {
        return UserEntity(
            id = dto.id,
            username = dto.username,
            email = dto.email,
            displayName = dto.displayName,
            bio = dto.bio ?: "",
            profilePictureUrl = dto.profilePictureUrl ?: "",
            followers = dto.followers ?: 0,
            following = dto.following ?: 0
        )
    }
}
