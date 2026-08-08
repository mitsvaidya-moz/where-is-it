package com.itemfinder.app.ui.navigation
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.itemfinder.app.ui.ItemFinderViewModel
import com.itemfinder.app.ui.screens.CaptureScreen
import com.itemfinder.app.ui.screens.CategoriesScreen
import com.itemfinder.app.ui.screens.ItemsScreen
import com.itemfinder.app.ui.screens.SearchScreen

sealed class Dest(val route: String, val label: String) {
    data object Search : Dest("search", "Find")
    data object Categories : Dest("categories", "Categories")
    data object Items : Dest("items", "Items")
    data object Capture : Dest("capture", "Add Place")
}

private val tabs = listOf(Dest.Search, Dest.Items, Dest.Categories, Dest.Capture)

@Composable
fun AppNavGraph(viewModel: ItemFinderViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination

                tabs.forEach { dest ->
                    val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(dest.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when (dest) {
                                    Dest.Search -> Icons.Filled.Search
                                    Dest.Items -> Icons.Filled.List
                                    Dest.Categories -> Icons.Filled.Category
                                    Dest.Capture -> Icons.Filled.CameraAlt
                                },
                                contentDescription = dest.label
                            )
                        },
                        label = { androidx.compose.material3.Text(dest.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Dest.Search.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Dest.Search.route) { SearchScreen(viewModel) }
            composable(Dest.Items.route) { ItemsScreen(viewModel) }
            composable(Dest.Categories.route) { CategoriesScreen(viewModel) }
            composable(Dest.Capture.route) { CaptureScreen(viewModel) }
        }
    }
}
