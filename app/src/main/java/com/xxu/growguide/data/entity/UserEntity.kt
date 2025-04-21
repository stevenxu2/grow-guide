package com.xxu.growguide.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Purpose: Entity class for storing user data in Room database
 */
@Entity(
    tableName = "users",
)
data class UserEntity(
    @PrimaryKey
    val userId: String, // Using Firebase Auth UID

    // Basic user info
    val email: String,
    val displayName: String,
    val profileImageUrl: String,

    // User stats
    val experienceLevel: String, // "Beginner", "Intermediate", "Advanced"
    val gardenCount: Int = 0,
    val plantCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,

    // Preferences
    val receiveNotifications: Boolean = true,
//    val darkMode: Boolean = false,

    // Timestamps
    val created: Long,
    val lastActive: Long
)