package com.example.instagramclone.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeysEntity(
    @PrimaryKey val id: String,
    val prevKey: String?,
    val nextKey: String?
)
