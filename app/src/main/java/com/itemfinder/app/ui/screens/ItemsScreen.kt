package com.itemfinder.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.itemfinder.app.data.Category
import com.itemfinder.app.data.Item
import com.itemfinder.app.ui.ItemFinderViewModel

@Composable
fun ItemsScreen(viewModel: ItemFinderViewModel) {
    val items by viewModel.items.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<Item?>(null) }
    var deletingItem by remember { mutableStateOf<Item?>(null) }

    fun categoryName(id: Long?): String =
        categories.firstOrNull { it.id == id }?.name ?: "No category"

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add item")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items, key = { it.id }) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name)
                            Text(
                                categoryName(item.categoryId),
                                style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                            )
                        }
                        IconButton(onClick = { editingItem = item }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { deletingItem = item }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ItemDialog(
            title = "New item",
            initialName = "",
            initialCategoryId = null,
            categories = categories,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, categoryId ->
                viewModel.addItem(name, categoryId)
                showAddDialog = false
            }
        )
    }

    editingItem?.let { item ->
        ItemDialog(
            title = "Edit item",
            initialName = item.name,
            initialCategoryId = item.categoryId,
            categories = categories,
            onDismiss = { editingItem = null },
            onConfirm = { name, categoryId ->
                viewModel.updateItem(item.copy(name = name, categoryId = categoryId))
                editingItem = null
            }
        )
    }

    deletingItem?.let { item ->
        AlertDialog(
            onDismissRequest = { deletingItem = null },
            title = { Text("Delete item?") },
            text = { Text("\"${item.name}\" and its storage location link will be removed.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteItem(item)
                    deletingItem = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { deletingItem = null }) { Text("Cancel") }
            }
        )
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ItemDialog(
    title: String,
    initialName: String,
    initialCategoryId: Long?,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onConfirm: (String, Long?) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var selectedCategoryId by remember { mutableStateOf(initialCategoryId) }
    var expanded by remember { mutableStateOf(false) }

    val selectedLabel = categories.firstOrNull { it.id == selectedCategoryId }?.name ?: "No category"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    label = { Text("Item name (e.g. Keys, Wallet, Hammer)") }
                )
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = selectedLabel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text("No category") },
                            onClick = {
                                selectedCategoryId = null
                                expanded = false
                            }
                        )
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategoryId = category.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = { onConfirm(name.trim(), selectedCategoryId) }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
