package com.example.qlghichu.Screen

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.qlghichu.ViewModel.GhiChuViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoteScreen(navController: NavController, viewModel: GhiChuViewModel = viewModel()) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var currentDateTime by remember {
        mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()))
    }

    // Undo/redo history
    val titleHistory = remember { mutableStateListOf<String>() }
    val contentHistory = remember { mutableStateListOf<String>() }
    var historyIndex by remember { mutableStateOf(-1) }

    fun saveToHistory(newTitle: String, newContent: String) {
        while (historyIndex < titleHistory.size - 1) {
            titleHistory.removeAt(titleHistory.size - 1)
            contentHistory.removeAt(contentHistory.size - 1)
        }
        titleHistory.add(newTitle)
        contentHistory.add(newContent)
        historyIndex++
    }

    LaunchedEffect(title, noteContent) {
        if (title.isNotEmpty() || noteContent.isNotEmpty()) {
            if (historyIndex == -1 || title != titleHistory.getOrNull(historyIndex) || noteContent != contentHistory.getOrNull(historyIndex)) {
                saveToHistory(title, noteContent)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tạo ghi chú") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (historyIndex > 0) {
                                historyIndex--
                                title = titleHistory[historyIndex]
                                noteContent = contentHistory[historyIndex]
                            }
                        },
                        enabled = historyIndex > 0
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = "Hoàn tác")
                    }
                    IconButton(
                        onClick = {
                            if (historyIndex < titleHistory.size - 1) {
                                historyIndex++
                                title = titleHistory[historyIndex]
                                noteContent = contentHistory[historyIndex]
                            }
                        },
                        enabled = historyIndex < titleHistory.size - 1
                    ) {
                        Icon(Icons.Default.Redo, contentDescription = "Làm lại")
                    }
                    IconButton(onClick = {
                        if (title.isNotBlank() || noteContent.isNotBlank()) {
                            viewModel.themGhiChu(title, noteContent, currentDateTime)
                        }
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Lưu")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                textStyle = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Bold),
                placeholder = {
                    Text("Tiêu đề", fontSize = 25.sp, color = Color.Gray.copy(alpha = 0.5f))
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = noteContent,
                onValueChange = { noteContent = it },
                textStyle = TextStyle(fontSize = 18.sp),
                placeholder = {
                    Text("Bắt đầu soạn", fontSize = 18.sp, color = Color.Gray.copy(alpha = 0.5f))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                )
            )
        }
    }
}