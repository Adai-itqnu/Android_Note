package com.example.qlghichu.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.qlghichu.ViewModel.GhiChuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashGhiChuScreen(
    viewModel: GhiChuViewModel = viewModel(),
    navController: NavController
) {
    val deletedNotes = viewModel.getDeletedGhiChu().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thùng rác ghi chú", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(deletedNotes.value) { note ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(note.tieuDe, fontWeight = FontWeight.Medium, maxLines = 1)
                        }
                        IconButton(onClick = { viewModel.restoreGhiChu(note.id) }) {
                            Icon(Icons.Default.Restore, contentDescription = "Khôi phục")
                        }
                        IconButton(onClick = { viewModel.deleteGhiChuForever(note.id) }) {
                            Icon(Icons.Default.DeleteForever, contentDescription = "Xóa vĩnh viễn")
                        }
                    }
                }
            }
        }
    }
}
