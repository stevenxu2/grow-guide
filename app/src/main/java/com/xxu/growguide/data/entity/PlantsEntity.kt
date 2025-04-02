package com.xxu.growguide.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity class for storing plant data in Room database
 */
@Entity(
    tableName = "plants",
    indices = [
        Index("commonName"),
        Index("scientificName")
    ]
)
data class PlantsEntity(
    @PrimaryKey
    val id: Int,

    // Basic info
    val commonName: String,
    val scientificName: String,
    val type: String,
    val family: String,
    val genus: String,
    val imageUrl: String,

    // Detailed info
    val description: String,
    val cycle: String,
    val watering: String,
    val sunlight: String,
    val maintenance: String,
    val growthRate: String,
    val droughtTolerant: Boolean,
    val saltTolerant: Boolean,
    val indoor: Boolean,
    val careLevel: String,
    val flowers: Boolean,
    val cones: Boolean,
    val fruits: Boolean,
    val leaf: Boolean,

    val isFavorite: Boolean = false, // will be highlight for listing
    val popularity: Int = 50, // General popularity score, higher will be more popular
    val timestamp: Long // When this entry was last updated
)