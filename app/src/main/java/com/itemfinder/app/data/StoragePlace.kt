package com.itemfinder.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a physical spot (e.g. "kitchen drawer", "hallway shelf") that was
 * captured with a photo. Multiple items can point to the same storage place.
 */
@Entity(tableName = "storage_places")
data class StoragePlace(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val photoPath: String,
    val label: String,
    val createdAt: Long = System.currentTimeMillis()
)
