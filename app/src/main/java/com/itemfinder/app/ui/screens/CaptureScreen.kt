package com.itemfinder.app.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.itemfinder.app.data.StoragePlace
import com.itemfinder.app.ui.ItemFinderViewModel
import java.io.File

private fun createImageUri(context: Context): Pair<File, Uri> {
    val photosDir = File(context.filesDir, "photos").apply { mkdirs() }
    val file = File(photosDir, "place_${System.currentTimeMillis()}.jpg")
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    return file to uri
}

@Composable
fun CaptureScreen(viewModel: ItemFinderViewModel) {
    val context = LocalContext.current
    val items by viewModel.items.collectAsState()
    val places by viewModel.storagePlaces.collectAsState()

    var pendingFile by remember { mutableStateOf<File?>(null) }
    var pendingUri by remember { mutableStateOf<Uri?>(null) }
    var showAssignDialog by remember { mutableStateOf(false) }
    var label by remember { mutableStateOf("") }
    var selectedItemIds by remember { mutableStateOf(setOf<Long>()) }
    var editingPlace by remember { mutableStateOf<StoragePlace?>(null) }
    var deletingPlace by remember { mutableStateOf<StoragePlace?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingFile != null) {
            showAssignDialog = true
        }
    }

    fun launchCamera() {
        val (file, uri) = createImageUri(context)
        pendingFile = file
        pendingUri = uri
        takePictureLauncher.launch(uri)
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = { launchCamera() }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.CameraAlt, contentDescription = null)
                androidx.compose.foundation.layout.Spacer(Modifier.padding(4.dp))
                Text("Take photo of a storage place")
            }

            Text("Saved storage places", style = MaterialTheme.typography.titleMedium)

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(places, key = { it.id }) { place ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(8.dp)) {
                            AsyncImage(
                                model = File(place.photoPath),
                                contentDescription = place.label,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(72.dp)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                            ) {
                                Text(place.label.ifBlank { "Untitled place" })
                                Text(
                                    "Tap to edit assigned items",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            IconButton(onClick = { deletingPlace = place }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete place")
                            }
                        }
                        Row(modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)) {
                            TextButton(onClick = { editingPlace = place }) { Text("Edit assigned items") }
                        }
                    }
                }
            }
        }
    }

    if (showAssignDialog && pendingFile != null) {
        AssignItemsDialog(
            title = "New storage place",
            initialLabel = "",
            allItems = items,
            initiallySelected = emptySet(),
            onDismiss = {
                showAssignDialog = false
                pendingFile?.delete()
                pendingFile = null
            },
            onConfirm = { newLabel, selectedIds ->
                viewModel.createStoragePlace(
                    photoPath = pendingFile!!.absolutePath,
                    label = newLabel,
                    assignedItemIds = selectedIds.toList()
                )
                showAssignDialog = false
                pendingFile = null
            }
        )
    }

    editingPlace?.let { place ->
        val currentlyAssigned = items.filter { it.storagePlaceId == place.id }.map { it.id }.toSet()
        AssignItemsDialog(
            title = "Edit storage place",
            initialLabel = place.label,
            allItems = items,
            initiallySelected = currentlyAssigned,
            onDismiss = { editingPlace = null },
            onConfirm = { newLabel, selectedIds ->
                viewModel.updateStoragePlaceLabel(place, newLabel)
                viewModel.updateStoragePlaceAssignments(place.id, selectedIds.toList(), items)
                editingPlace = null
            }
        )
    }

    deletingPlace?.let { place ->
        AlertDialog(
            onDismissRequest = { deletingPlace = null },
            title = { Text("Delete storage place?") },
            text = { Text("The photo will be removed and its items will become unassigned.") },
            confirmButton = {
                TextButton(onClick = {
                    File(place.photoPath).delete()
                    viewModel.deleteStoragePlace(place)
                    deletingPlace = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { deletingPlace = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun AssignItemsDialog(
    title: String,
    initialLabel: String,
    allItems: List<com.itemfinder.app.data.Item>,
    initiallySelected: Set<Long>,
    onDismiss: () -> Unit,
    onConfirm: (String, Set<Long>) -> Unit
) {
    var label by remember { mutableStateOf(initialLabel) }
    var selected by remember { mutableStateOf(initiallySelected) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    singleLine = true,
                    label = { Text("Label (e.g. Kitchen drawer)") }
                )
                Text("Select the items kept here:", style = MaterialTheme.typography.bodyMedium)
                LazyColumn(modifier = Modifier.height(220.dp)) {
                    items(allItems, key = { it.id }) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selected.contains(item.id),
                                onCheckedChange = { checked ->
                                    selected = if (checked) selected + item.id else selected - item.id
                                }
                            )
                            Text(item.name)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(label.trim(), selected) }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
