package com.example.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.RoomMateXViewModel
import com.example.ui.chores.ChoreCard
import com.example.ui.components.SectionHeader
import com.example.ui.expenses.ExpenseCard
import com.example.ui.shopping.ShoppingItemRow
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchScreen(
    viewModel: RoomMateXViewModel,
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val expenses by viewModel.expenses.collectAsState()
    val chores by viewModel.chores.collectAsState()
    val shoppingItems by viewModel.shoppingItems.collectAsState()

    val filteredExpenses = expenses.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)
    }

    val filteredChores = chores.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.assignedToName.contains(searchQuery, ignoreCase = true)
    }

    val filteredShopping = shoppingItems.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search chores, expenses, shopping...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ElectricCyan) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                                }
                            }
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { insets ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .padding(horizontal = 18.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (searchQuery.isBlank()) {
                item {
                    Text(
                        text = "Type a term above to search across your household database.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                if (filteredExpenses.isNotEmpty()) {
                    item { SectionHeader(title = "Expenses (${filteredExpenses.size})") }
                    items(filteredExpenses) { expense ->
                        ExpenseCard(expense = expense)
                    }
                }

                if (filteredChores.isNotEmpty()) {
                    item { SectionHeader(title = "Chores (${filteredChores.size})") }
                    items(filteredChores) { chore ->
                        ChoreCard(chore = chore, onComplete = { viewModel.completeChore(chore) })
                    }
                }

                if (filteredShopping.isNotEmpty()) {
                    item { SectionHeader(title = "Shopping (${filteredShopping.size})") }
                    items(filteredShopping) { item ->
                        ShoppingItemRow(
                            item = item,
                            onToggle = { viewModel.toggleShoppingItem(item.id, !item.isCompleted) },
                            onDelete = { viewModel.deleteShoppingItem(item.id) }
                        )
                    }
                }
            }
        }
    }
}
