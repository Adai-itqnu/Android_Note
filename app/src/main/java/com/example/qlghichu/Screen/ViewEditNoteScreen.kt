package com.example.qlghichu.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.qlghichu.Entity.GhiChu
import com.example.qlghichu.ViewModel.GhiChuViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewEditNoteScreen(
    navController: NavController,
    noteId: Int,
    viewModel: GhiChuViewModel = viewModel()
) {
    var ghiChu by remember { mutableStateOf<GhiChu?>(null) }
    var title by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var originalTitle by remember { mutableStateOf("") }
    var originalContent by remember { mutableStateOf("") }
    var isChanged by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Load ghi chú khi khởi tạo
    LaunchedEffect(noteId) {
        scope.launch {
            val note = viewModel.getGhiChuById(noteId)
            note?.let {
                ghiChu = it
                title = it.tieuDe
                noteContent = it.noiDung
                originalTitle = it.tieuDe
                originalContent = it.noiDung
            }
        }
    }

    // Kiểm tra thay đổi
    LaunchedEffect(title, noteContent) {
        isChanged = title != originalTitle || noteContent != originalContent
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Xem ghi chú") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    // Nút xóa
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Red)
                    }

                    // Nút lưu (chỉ hiện khi có thay đổi)
                    if (isChanged) {
                        IconButton(onClick = {
                            ghiChu?.let { currentNote ->
                                val updatedNote = currentNote.copy(
                                    tieuDe = title,
                                    noiDung = noteContent,
                                    ngay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                                )
                                viewModel.updateGhiChu(updatedNote)
                                navController.popBackStack()
                            }
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Lưu")
                        }
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
                    Text("Nội dung ghi chú", fontSize = 18.sp, color = Color.Gray.copy(alpha = 0.5f))
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

    // Dialog xác nhận xóa
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa ghi chú này không?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        ghiChu?.let {
                            viewModel.deleteGhiChu(it)
                            navController.popBackStack()
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Xóa", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}