package com.itemfinder.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itemfinder.app.data.Category
import com.itemfinder.app.data.Item
import com.itemfinder.app.data.ItemFinderRepository
import com.itemfinder.app.data.ItemWithDetails
import com.itemfinder.app.data.StoragePlace
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ItemFinderViewModel(private val repo: ItemFinderRepository) : ViewModel() {

    val categories: StateFlow<List<Category>> =
        repo.categories.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val items: StateFlow<List<Item>> =
        repo.items.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val storagePlaces: StateFlow<List<StoragePlace>> =
        repo.storagePlaces.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val searchQuery = MutableStateFlow("")
    val query: StateFlow<String> = searchQuery

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<ItemWithDetails>> =
        searchQuery.flatMapLatest { q -> repo.searchItems(q) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setQuery(q: String) {
        searchQuery.value = q
    }

    // Category actions
    fun addCategory(name: String) = viewModelScope.launch { repo.addCategory(name) }
    fun updateCategory(category: Category) = viewModelScope.launch { repo.updateCategory(category) }
    fun deleteCategory(category: Category) = viewModelScope.launch { repo.deleteCategory(category) }

    // Item actions
    fun addItem(name: String, categoryId: Long?) = viewModelScope.launch { repo.addItem(name, categoryId) }
    fun updateItem(item: Item) = viewModelScope.launch { repo.updateItem(item) }
    fun deleteItem(item: Item) = viewModelScope.launch { repo.deleteItem(item) }

    // Storage place actions
    fun createStoragePlace(
        photoPath: String,
        label: String,
        assignedItemIds: List<Long>,
        onCreated: (Long) -> Unit = {}
    ) = viewModelScope.launch {
        val id = repo.addStoragePlace(photoPath, label)
        if (assignedItemIds.isNotEmpty()) {
            repo.assignItemsToPlace(assignedItemIds, id)
        }
        onCreated(id)
    }

    fun updateStoragePlaceAssignments(placeId: Long, newItemIds: List<Long>, allItems: List<Item>) =
        viewModelScope.launch {
            // Unassign items that used to point to this place but are no longer selected
            val toUnassign = allItems.filter { it.storagePlaceId == placeId && it.id !in newItemIds }.map { it.id }
            if (toUnassign.isNotEmpty()) repo.assignItemsToPlace(toUnassign, null)
            if (newItemIds.isNotEmpty()) repo.assignItemsToPlace(newItemIds, placeId)
        }

    fun updateStoragePlaceLabel(place: StoragePlace, newLabel: String) =
        viewModelScope.launch { repo.updateStoragePlace(place.copy(label = newLabel)) }

    fun deleteStoragePlace(place: StoragePlace) = viewModelScope.launch { repo.deleteStoragePlace(place) }
}
