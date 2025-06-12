    package com.example.qlghichu.Screen

    import android.app.DatePickerDialog
    import android.app.TimePickerDialog
    import android.widget.DatePicker
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.shape.RoundedCornerShape
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
    import com.example.qlghichu.Entity.NhiemVu
    import com.example.qlghichu.ViewModel.GhiChuViewModel
    import kotlinx.coroutines.launch
    import java.text.SimpleDateFormat
    import java.util.*

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ViewEditTaskScreen(
        navController: NavController,
        taskId: Int,
        viewModel: GhiChuViewModel = viewModel()
    ) {
        val context = LocalContext.current
        var task by remember { mutableStateOf<NhiemVu?>(null) }
        var taskText by remember { mutableStateOf("") }
        var selectedDateTime by remember { mutableStateOf(getCurrentDateTimeWithOffset()) }
        var status by remember { mutableStateOf("Chưa hoàn thành") }
        var isPinned by remember { mutableStateOf(false) }
        var showDeleteDialog by remember { mutableStateOf(false) }

        val subTasks by viewModel.getSubTasksForTask(taskId).collectAsState(initial = emptyList())
        var newSubTaskTitle by remember { mutableStateOf("") }

        val scope = rememberCoroutineScope()
        val calendar = Calendar.getInstance()

        // Load task
        LaunchedEffect(taskId) {
            scope.launch {
                val fetched = viewModel.getNhiemVuById(taskId)
                fetched?.let {
                    task = it
                    taskText = it.tieuDe
                    selectedDateTime = it.ngay
                    status = it.trangThai
                    isPinned = it.isPinned
                    try {
                        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        val date = sdf.parse(it.ngay)
                        date?.let { d -> calendar.time = d }
                    } catch (_: Exception) {}
                }
            }
        }

        // DatePickerDialog để chọn ngày & giờ mới
        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                calendar.set(year, month, day)
                // Sau khi chọn ngày, hiển thị TimePickerDialog
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
                            "Sửa nhiệm vụ",
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
                    },
                    actions = {
                        // Xóa mềm vào thùng rác
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color(0xFFCC9966))
                        }
                        // Lưu
                        IconButton(
                            onClick = {
                                task?.let { t ->
                                    viewModel.updateNhiemVu(
                                        t.copy(
                                            tieuDe = taskText,
                                            trangThai = status,
                                            ngay = selectedDateTime,
                                            isPinned = isPinned
                                        )
                                    )
                                }
                                navController.popBackStack()
                            },
                            enabled = taskText.isNotBlank()
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Lưu")
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
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
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

                // Nút chọn ngày giờ
                Button(
                    onClick = { datePickerDialog.show() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F1F1)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AccessTime, contentDescription = "Đặt nhắc nhở")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedDateTime.isBlank())
                            "Đặt nhắc nhở"
                        else
                            "Nhắc nhở: $selectedDateTime",
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // CheckBox hoàn thành (nếu không dùng subtask)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = status == "Hoàn thành",
                        onCheckedChange = { checked ->
                            status = if (checked) "Hoàn thành" else "Chưa hoàn thành"
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF3A2513),
                            checkmarkColor = Color.White
                        )
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Hoàn thành", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Subtasks
                Text("Các công việc con (Subtask):", fontWeight = FontWeight.Bold)
                subTasks.forEach { subTask ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = subTask.isCompleted,
                            onCheckedChange = { checked ->
                                viewModel.updateSubTask(subTask.copy(isCompleted = checked))
                            }
                        )
                        Text(subTask.title, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.deleteSubTask(subTask) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Xóa subtask")
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = newSubTaskTitle,
                        onValueChange = { newSubTaskTitle = it },
                        placeholder = { Text("Thêm subtask...") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        if (newSubTaskTitle.isNotBlank()) {
                            viewModel.addSubTask(taskId, newSubTaskTitle)
                            newSubTaskTitle = ""
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Thêm")
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Xác nhận xóa") },
                text = { Text("Bạn có chắc chắn muốn đưa nhiệm vụ vào thùng rác không?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            task?.let { viewModel.deleteNhiemVu(it) }
                            showDeleteDialog = false
                            navController.popBackStack()
                        }
                    ) {
                        Text("Đưa vào thùng rác", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Hủy") }
                }
            )
        }
    }

    private fun getCurrentDateTimeWithOffset(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, 1)
        return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(calendar.time)
    }
