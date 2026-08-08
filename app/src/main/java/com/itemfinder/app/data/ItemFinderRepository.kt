package com.itemfinder.app.data

import kotlinx.coroutines.flow.Flow

class ItemFinderRepository(private val db: AppDatabase) {

    // Categories
    val categories: Flow<List<Category>> = db.categoryDao().getAll()
    suspend fun addCategory(name: String) = db.categoryDao().insert(Category(name = name))
    suspend fun updateCategory(category: Category) = db.categoryDao().update(category)
    suspend fun deleteCategory(category: Category) = db.categoryDao().delete(category)

    // Items
    val items: Flow<List<Item>> = db.itemDao().getAll()
    suspend fun addItem(name: String, categoryId: Long?): Long =
        db.itemDao().insert(Item(name = name, categoryId = categoryId))
    suspend fun updateItem(item: Item) = db.itemDao().update(item)
    suspend fun deleteItem(item: Item) = db.itemDao().delete(item)
    fun searchItems(query: String): Flow<List<ItemWithDetails>> = db.itemDao().search(query)
    fun itemsForPlace(placeId: Long): Flow<List<ItemWithDetails>> =
        db.itemDao().getItemsForStoragePlace(placeId)
    suspend fun assignItemsToPlace(itemIds: List<Long>, placeId: Long?) =
        db.itemDao().assignStoragePlace(itemIds, placeId)

    // Storage places
    val storagePlaces: Flow<List<StoragePlace>> = db.storagePlaceDao().getAll()
    suspend fun addStoragePlace(photoPath: String, label: String): Long =
        db.storagePlaceDao().insert(StoragePlace(photoPath = photoPath, label = label))
    suspend fun updateStoragePlace(place: StoragePlace) = db.storagePlaceDao().update(place)
    suspend fun deleteStoragePlace(place: StoragePlace) = db.storagePlaceDao().delete(place)
}
