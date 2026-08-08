package com.itemfinder.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = StoragePlace::class,
            parentColumns = ["id"],
            childColumns = ["storagePlaceId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val categoryId: Long? = null,
    val storagePlaceId: Long? = null
)

/** Flattened view used by the UI: item + its category name + its storage photo/label. */
data class ItemWithDetails(
    val itemId: Long,
    val itemName: String,
    val categoryId: Long?,
    val categoryName: String?,
    val storagePlaceId: Long?,
    val photoPath: String?,
    val placeLabel: String?
)
