package com.example.qlghichu.Screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.qlghichu.Entity.SubTask
import com.example.qlghichu.ViewModel.GhiChuViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    navController: NavController,
    viewModel: GhiChuViewModel = viewModel()
) {
    val context = LocalContext.current
    var taskText by remember { mutableStateOf("") }
    var selectedDateTime by remember { mutableStateOf(getCurrentDateTimeWithOffset()) }
    var status by remember { mutableStateOf("Chưa hoàn thành") }
    var newSubTaskTitle by remember { mutableStateOf("") }
    var subTasks by remember { mutableStateOf<List<SubTask>>(emptyList()) }
    val scope = rememberCoroutineScope()

    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            calendar.set(year, month, day)
            val timePickerDialog = TimePickerDialog(
                context,
                { _, hour: Int, minute: Int ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    selectedDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(calendar.time)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tạo nhiệm vụ",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(start = 16.dp, top = 30.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color.DarkGray)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            TextField(
                value = taskText,
                onValueChange = { taskText = it },
                placeholder = { Text("Tiêu đề nhiệm vụ") },
                textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { }),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { datePickerDialog.show() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F1F1)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AccessTime, contentDescription = "Đặt nhắc nhở")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedDateTime.isBlank()) "Đặt nhắc nhở" else "Nhắc nhở: $selectedDateTime",
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Các công việc con (Subtask):", fontWeight = FontWeight.Bold)

            subTasks.forEachIndexed { index, subTask ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = subTask.isCompleted,
                        onCheckedChange = { checked ->
                            subTasks = subTasks.toMutableList().also { list ->
                                list[index] = list[index].copy(isCompleted = checked)
                            }
                        }
                    )
                    Text(subTask.title, modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        subTasks = subTasks.toMutableList().also { it.removeAt(index) }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa subtask")
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = newSubTaskTitle,
                    onValueChange = { newSubTaskTitle = it },
                    placeholder = { Text("Thêm subtask...") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (newSubTaskTitle.isNotBlank()) {
                                subTasks = subTasks + SubTask(title = newSubTaskTitle, isCompleted = false, taskId = 0)
                                newSubTaskTitle = ""
                            }
                        }
                    )
                )
                IconButton(onClick = {
                    if (newSubTaskTitle.isNotBlank()) {
                        subTasks = subTasks + SubTask(title = newSubTaskTitle, isCompleted = false, taskId = 0)
                        newSubTaskTitle = ""
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (taskText.isBlank()) return@Button

                    scope.launch {
                        viewModel.themNhiemVu(
                            tieuDe = taskText,
                            trangThai = status,
                            ngay = selectedDateTime,
                            onSuccess = { taskId ->
                                subTasks.forEach { subTask ->
                                    viewModel.themSubTask(subTask.copy(taskId = taskId))
                                }
                                navController.popBackStack()
                            }
                        )
                    }
                },
                enabled = taskText.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Hoàn tất")
            }
        }
    }
}

private fun getCurrentDateTimeWithOffset(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MINUTE, 1)
    return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(calendar.time)
}
