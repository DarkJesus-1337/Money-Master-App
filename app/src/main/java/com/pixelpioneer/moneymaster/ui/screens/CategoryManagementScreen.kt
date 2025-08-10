package com.pixelpioneer.moneymaster.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pixelpioneer.moneymaster.core.util.UiState
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.ui.viewmodel.CategoryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    viewModel: CategoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val categoriesState by viewModel.categoriesState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<TransactionCategory?>(null) }
    var showDeleteDialog by remember { mutableStateOf<TransactionCategory?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kategorien verwalten") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zurück")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Kategorie hinzufügen")
            }
        }
    ) { paddingValues ->
        when (val state = categoriesState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Success -> {
                val categories = state.data
                if (categories.isEmpty()) {
                    EmptyState(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                } else {
                    CategoryList(
                        categories = categories,
                        modifier = Modifier.padding(paddingValues),
                        onEditCategory = { category ->
                            categoryToEdit = category
                            showAddDialog = true
                        },
                        onDeleteCategory = { category ->
                            // Prüfen ob es eine vordefinierte Kategorie ist (ID <= 10)
                            if (category.id <= 10) {
                                // Zeige eine Warnung, dass vordefinierte Kategorien nicht gelöscht werden können
                                // Sie könnten hier einen Toast oder Snackbar zeigen
                            } else {
                                showDeleteDialog = category
                            }
                        }
                    )
                }
            }
            is UiState.Error -> {
                ErrorState(
                    message = state.message,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onRetry = { viewModel.refreshCategories() }
                )
            }
            is UiState.Empty -> {
                EmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }

    // Dialog zum Hinzufügen/Bearbeiten von Kategorien
    if (showAddDialog) {
        AddEditCategoryDialog(
            category = categoryToEdit,
            onDismiss = {
                showAddDialog = false
                categoryToEdit = null
            },
            onSave = { name, color ->
                scope.launch {
                    if (categoryToEdit != null) {
                        viewModel.updateCategory(
                            categoryToEdit!!.copy(name = name, color = color)
                        )
                    } else {
                        viewModel.addCategory(name, color)
                    }
                    showAddDialog = false
                    categoryToEdit = null
                }
            }
        )
    }

    // Lösch-Bestätigungsdialog
    showDeleteDialog?.let { category ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Kategorie löschen") },
            text = {
                Text("Möchten Sie die Kategorie \"${category.name}\" wirklich löschen? Alle Transaktionen mit dieser Kategorie werden auf \"Sonstiges\" umgestellt.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            viewModel.deleteCategory(category)
                            showDeleteDialog = null
                        }
                    }
                ) {
                    Text("Löschen", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Abbrechen")
                }
            }
        )
    }
}

@Composable
fun CategoryList(
    categories: List<TransactionCategory>,
    modifier: Modifier = Modifier,
    onEditCategory: (TransactionCategory) -> Unit,
    onDeleteCategory: (TransactionCategory) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Vordefinierte Kategorien (nicht löschbar)
        item {
            Text(
                text = "Vordefinierte Kategorien",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items(categories.filter { it.id <= 10 }) { category ->
            CategoryItem(
                category = category,
                isPredefined = true,
                onEdit = { onEditCategory(category) },
                onDelete = { /* Vordefinierte können nicht gelöscht werden */ }
            )
        }

        // Eigene Kategorien (löschbar)
        val customCategories = categories.filter { it.id > 10 }
        if (customCategories.isNotEmpty()) {
            item {
                Text(
                    text = "Eigene Kategorien",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            items(customCategories) { category ->
                CategoryItem(
                    category = category,
                    isPredefined = false,
                    onEdit = { onEditCategory(category) },
                    onDelete = { onDeleteCategory(category) }
                )
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: TransactionCategory,
    isPredefined: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEdit() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Farbindikator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(category.color))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Kategoriename
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            // Badges für Typ
            if (isPredefined) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = "Standard",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // Aktionen
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Bearbeiten",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                if (!isPredefined) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Löschen",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditCategoryDialog(
    category: TransactionCategory?,
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var selectedColor by remember { mutableStateOf(category?.color ?: predefinedColors.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (category != null) "Kategorie bearbeiten" else "Neue Kategorie"
            )
        },
        text = {
            Column {
                // Name-Eingabefeld
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Kategoriename") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Farbauswahl
                Text(
                    text = "Farbe wählen",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ColorPicker(
                    selectedColor = selectedColor,
                    onColorSelected = { selectedColor = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name.trim(), selectedColor)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Speichern")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}

@Composable
fun ColorPicker(
    selectedColor: Int,
    onColorSelected: (Int) -> Unit
) {
    Column {
        for (row in predefinedColors.chunked(5)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (color in row) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(color))
                            .clickable { onColorSelected(color) }
                            .padding(4.dp)
                    ) {
                        if (color == selectedColor) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Ausgewählt",
                                tint = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Category,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Keine Kategorien vorhanden",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ErrorState(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Erneut versuchen")
            }
        }
    }
}

// Vordefinierte Farben für die Farbauswahl
private val predefinedColors = listOf(
    0xFF4CAF50.toInt(), // Grün
    0xFF2196F3.toInt(), // Blau
    0xFF9C27B0.toInt(), // Lila
    0xFFFF9800.toInt(), // Orange
    0xFFF44336.toInt(), // Rot
    0xFFE91E63.toInt(), // Pink
    0xFF3F51B5.toInt(), // Indigo
    0xFF00BCD4.toInt(), // Cyan
    0xFF8BC34A.toInt(), // Hellgrün
    0xFF607D8B.toInt(), // Blaugrau
    0xFFFFEB3B.toInt(), // Gelb
    0xFF795548.toInt(), // Braun
    0xFF9E9E9E.toInt(), // Grau
    0xFFFF5722.toInt(), // Deep Orange
    0xFF673AB7.toInt()  // Deep Purple
)