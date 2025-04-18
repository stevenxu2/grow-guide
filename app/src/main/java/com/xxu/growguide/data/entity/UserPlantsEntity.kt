package com.xxu.growguide.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity class for storing user's plants in Room database
 * Links plants from the catalog to a specific user's garden
 */
@Entity(
    tableName = "user_plants",
    foreignKeys = [
        ForeignKey(
            entity = PlantsEntity::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("plantId"),
        Index("userId"),
        Index(value = ["plantId", "userId"], unique = true)
    ]
)
data class UserPlantsEntity(
    @PrimaryKey(autoGenerate = true)
    val userPlantId: Long = 0,

    val userId: String,
    val plantId: Int,

    // Custom fields for the user's plant
    val nickname: String? = null,
    val plantingDate: Long, // Date in milliseconds
    val imageUri: String? = null,
    val notes: String? = null,

    // Plant status tracking
    val lastWateredDate: Long? = null,
    val lastFertilizedDate: Long? = null,

    val dateAdded: Long = System.currentTimeMillis()
)