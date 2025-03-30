package com.xxu.growguide.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database entity for storing plants information
 */
@Entity(tableName = "plants")
data class PlantsEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val createdDate: Long
)