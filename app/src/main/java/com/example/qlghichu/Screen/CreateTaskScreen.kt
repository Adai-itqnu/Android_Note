package com.example.qlghichu.Screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
    var taskList by remember { mutableStateOf<List<String>>(emptyList()) }
    var subTasks by remember { mutableStateOf<List<SubTask>>(emptyList()) }
    var newSubTaskTitle by remember { mutableStateOf("") }
    var selectedDateTime by remember { mutableStateOf(getCurrentDateTimeWithOffset()) }

    val calendar = Calendar.getInstance()

    // DatePickerDialog để chọn ngày & giờ
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
                        "Nhiệm vụ",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(start = 16.dp, top = 30.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.DarkGray
                        )
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
            // Hiển thị danh sách nhiệm vụ đã thêm
            taskList.forEach { task ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Checkbox(checked = false, onCheckedChange = {})
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(task, fontSize = 18.sp)
                }
            }

            // Ô nhập nhiệm vụ mới
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = false, onCheckedChange = {})
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = taskText,
                            onValueChange = { taskText = it },
                            placeholder = { Text("Chạm \"Enter\" để tạo nhiệm vụ") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (taskText.isNotBlank()) {
                                        taskList = listOf(taskText) + taskList
                                        taskText = ""
                                    }
                                }
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nút đặt nhắc nhở
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { datePickerDialog.show() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F1F1)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.AccessTime, contentDescription = "Đặt nhắc nhở")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedDateTime == getCurrentDateTimeWithOffset())
                                    "Đặt nhắc nhở"
                                else
                                    "Nhắc nhở: $selectedDateTime",
                                color = Color.Black,
                                fontSize = 14.sp
                            )
                        }

                        TextButton(
                            onClick = {
                                // Lưu tất cả nhiệm vụ trong taskList
                                taskList.forEach { task ->
                                    viewModel.themNhiemVu(
                                        tieuDe = task,
                                        trangThai = "Chưa hoàn thành",
                                        ngay = selectedDateTime,
                                        onSuccess = {}
                                    )
                                }
                                if (taskText.isNotBlank()) {
                                    viewModel.themNhiemVu(
                                        tieuDe = taskText,
                                        trangThai = "Chưa hoàn thành",
                                        ngay = selectedDateTime,
                                        onSuccess = {}
                                    )
                                }
                                // Lưu subtask có thể làm sau (nếu cần nhận được taskId, nên dùng onSuccess để callback)

                                navController.popBackStack()
                            },
                            enabled = taskList.isNotEmpty() || taskText.isNotBlank()
                        ) {
                            Text("Hoàn tất", fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- PHẦN QUẢN LÝ SUBTASK ---
                    Text("Các công việc con (Subtask):", fontWeight = FontWeight.Bold)

                    subTasks.forEachIndexed { index, subTask ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = subTask.isCompleted,
                                onCheckedChange = { checked ->
                                    subTasks = subTasks.toMutableList().also { list ->
                                        list[index] = list[index].copy(isCompleted = checked)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(subTask.title, modifier = Modifier.weight(1f))
                            IconButton(onClick = {
                                subTasks = subTasks.toMutableList().also { it.removeAt(index) }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Xóa subtask")
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = newSubTaskTitle,
                            onValueChange = { newSubTaskTitle = it },
                            placeholder = { Text("Thêm subtask...") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                            ),
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
                }
            }
        }
    }
}

private fun getCurrentDateTimeWithOffset(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MINUTE, 1) // Default to 1 minute ahead
    return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(calendar.time)
}
