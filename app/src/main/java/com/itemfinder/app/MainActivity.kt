package com.itemfinder.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.itemfinder.app.data.AppDatabase
import com.itemfinder.app.data.ItemFinderRepository
import com.itemfinder.app.ui.ItemFinderViewModel
import com.itemfinder.app.ui.navigation.AppNavGraph
import com.itemfinder.app.ui.theme.ItemFinderTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ItemFinderViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val db = AppDatabase.getInstance(applicationContext)
                val repo = ItemFinderRepository(db)
                @Suppress("UNCHECKED_CAST")
                return ItemFinderViewModel(repo) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ItemFinderTheme {
                AppNavGraph(viewModel)
            }
        }
    }
}
