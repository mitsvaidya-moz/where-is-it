package com.itemfinder.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAll(): Flow<List<Category>>

    @Insert
    suspend fun insert(category: Category): Long

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)
}

@Dao
interface StoragePlaceDao {
    @Query("SELECT * FROM storage_places ORDER BY createdAt DESC")
    fun getAll(): Flow<List<StoragePlace>>

    @Query("SELECT * FROM storage_places WHERE id = :id")
    suspend fun getById(id: Long): StoragePlace?

    @Insert
    suspend fun insert(place: StoragePlace): Long

    @Update
    suspend fun update(place: StoragePlace)

    @Delete
    suspend fun delete(place: StoragePlace)
}

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY name ASC")
    fun getAll(): Flow<List<Item>>

    @Insert
    suspend fun insert(item: Item): Long

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("UPDATE items SET storagePlaceId = :storagePlaceId WHERE id IN (:itemIds)")
    suspend fun assignStoragePlace(itemIds: List<Long>, storagePlaceId: Long?)

    @Query(
        """
        SELECT
            item.id AS itemId,
            item.name AS itemName,
            item.categoryId AS categoryId,
            category.name AS categoryName,
            item.storagePlaceId AS storagePlaceId,
            place.photoPath AS photoPath,
            place.label AS placeLabel
        FROM items AS item
        LEFT JOIN categories AS category ON item.categoryId = category.id
        LEFT JOIN storage_places AS place ON item.storagePlaceId = place.id
        WHERE item.name LIKE '%' || :query || '%'
        ORDER BY item.name ASC
        """
    )
    fun search(query: String): Flow<List<ItemWithDetails>>

    @Query(
        """
        SELECT
            item.id AS itemId,
            item.name AS itemName,
            item.categoryId AS categoryId,
            category.name AS categoryName,
            item.storagePlaceId AS storagePlaceId,
            place.photoPath AS photoPath,
            place.label AS placeLabel
        FROM items AS item
        LEFT JOIN categories AS category ON item.categoryId = category.id
        LEFT JOIN storage_places AS place ON item.storagePlaceId = place.id
        WHERE place.id = :storagePlaceId
        ORDER BY item.name ASC
        """
    )
    fun getItemsForStoragePlace(storagePlaceId: Long): Flow<List<ItemWithDetails>>
}
