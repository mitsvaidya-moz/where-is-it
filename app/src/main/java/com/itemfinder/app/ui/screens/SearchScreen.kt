package com.itemfinder.app.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.itemfinder.app.ui.ItemFinderViewModel
import java.io.File

@Composable
fun SearchScreen(viewModel: ItemFinderViewModel) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.searchResults.collectAsState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.setQuery(it) },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                label = { Text("Search for keys, wallet, hammer...") },
                modifier = Modifier.fillMaxWidth()
            )

            if (results.isEmpty()) {
                Text(
                    if (query.isBlank()) "Type to search, or browse everything below."
                    else "No items match \"$query\".",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(results, key = { it.itemId }) { result ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (result.photoPath != null) {
                                AsyncImage(
                                    model = File(result.photoPath),
                                    contentDescription = result.placeLabel,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .height(72.dp)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            }
                            Column(modifier = Modifier.padding(start = 12.dp)) {
                                Text(result.itemName, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    result.categoryName ?: "No category",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    if (result.photoPath != null)
                                        "Stored at: ${result.placeLabel?.ifBlank { "Untitled place" }}"
                                    else "No storage location set yet",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
