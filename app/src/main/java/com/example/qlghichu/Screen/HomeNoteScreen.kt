package com.example.qlghichu.Screen

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.qlghichu.Entity.GhiChu
import com.example.qlghichu.Entity.NhiemVu
import com.example.qlghichu.Entity.SubTask
import com.example.qlghichu.ViewModel.GhiChuViewModel
import com.example.qlghichu.ViewModel.NavigationViewModel
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

val HighlightColor = Color(0xFFFFE4D1)
val IconBackground = Color(0xFF3A2513)
val ButtonTextColor = Color.White
val FabYellow = Color(0xFFFFC107)

@Composable
fun TaskTabBar(selected: String, onSelected: (String) -> Unit, fontSize: Int = 16) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 0.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        listOf("Chưa hoàn thành" to "uncompleted", "Đã hoàn thành" to "completed").forEach { (text, key) ->
            val selected = selected == key
            val animColor by animateColorAsState(if (selected) IconBackground else Color.Gray)
            val animScale by animateFloatAsState(if (selected) 1.1f else 1f)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onSelected(key) }
                    .padding(vertical = 0.dp)
                    .scale(animScale),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = text, fontSize = fontSize.sp, fontWeight = FontWeight.Bold, color = animColor)
                if (selected) {
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .height(4.dp)
                            .width(32.dp)
                            .background(HighlightColor, RoundedCornerShape(8.dp))
                    )
                } else {
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun HomeNoteScreen(
    navController: NavController,
    ghiChuViewModel: GhiChuViewModel = viewModel(),
    navViewModel: NavigationViewModel = viewModel()
) {
    val context = LocalContext.current

    var searchGhiChu by remember { mutableStateOf("") }
    var searchNhiemVu by remember { mutableStateOf("") }
    var ghiChuList by remember { mutableStateOf<List<GhiChu>>(emptyList()) }
    var nhiemVuList by remember { mutableStateOf<List<NhiemVu>>(emptyList()) }
    val selectedTab by navViewModel.selectedTab.collectAsState()

    var isNoteSelectionMode by remember { mutableStateOf(false) }
    var selectedNoteIds by remember { mutableStateOf(listOf<Int>()) }
    var isTaskSelectionMode by remember { mutableStateOf(false) }
    var selectedTaskIds by remember { mutableStateOf(listOf<Int>()) }
    var selectedTaskTab by remember { mutableStateOf("uncompleted") }

    val (completedTasks, uncompletedTasks) = nhiemVuList.partition { it.trangThai == "Hoàn thành" }

    LaunchedEffect(searchGhiChu) {
        ghiChuViewModel.getAllGhiChu(searchGhiChu).collectLatest { list ->
            ghiChuList = list.sortedWith(compareByDescending<GhiChu> { it.isPinned }.thenByDescending { it.id })
        }
    }
    LaunchedEffect(searchNhiemVu) {
        ghiChuViewModel.getAllNhiemVu(searchNhiemVu).collectLatest { list ->
            nhiemVuList = list.sortedWith(compareByDescending<NhiemVu> { it.isPinned }.thenByDescending { it.id })
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (selectedTab == "Ghi chú") "Ghi Chú" else "Nhiệm vụ",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(start = 16.dp, top = 30.dp)
                    )
                },
                actions = {
                    IconButton(onClick = {
                        if (selectedTab == "Ghi chú") {
                            navController.navigate("trash/ghi_chu")
                        } else {
                            navController.navigate("trash/nhiem_vu")
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Thùng rác", tint = IconBackground)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(selectedTab = selectedTab, onTabSelected = { navViewModel.setSelectedTab(it) })
        },
        floatingActionButton = {
            var fabPressed by remember { mutableStateOf(false) }
            val fabScale by animateFloatAsState(targetValue = if (fabPressed) 0.85f else 1f, tween(150))

            if (!isNoteSelectionMode && !isTaskSelectionMode) {
                FloatingActionButton(
                    onClick = {
                        fabPressed = true
                        when (selectedTab) {
                            "Ghi chú" -> navController.navigate("create")
                            "Nhiệm vụ" -> navController.navigate("createtask")
                        }
                    },
                    containerColor = FabYellow,
                    shape = RoundedCornerShape(52.dp),
                    modifier = Modifier.scale(fabScale)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm", tint = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = if (selectedTab == "Ghi chú") searchGhiChu else searchNhiemVu,
                onValueChange = {
                    if (selectedTab == "Ghi chú") searchGhiChu = it else searchNhiemVu = it
                },
                textStyle = TextStyle(fontSize = 16.sp),
                placeholder = {
                    Text(if (selectedTab == "Ghi chú") "Tìm Kiếm Ghi chú" else "Tìm Kiếm Nhiệm vụ", fontSize = 16.sp)
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = Color.Gray) },
                singleLine = true,
                shape = RoundedCornerShape(52.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp, start = 30.dp, end = 30.dp).height(52.dp)
            )

            if (selectedTab == "Nhiệm vụ") {
                TaskTabBar(selectedTaskTab, { selectedTaskTab = it }, fontSize = 16)
            }

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    slideInHorizontally { fullWidth -> if (targetState == "Nhiệm vụ") fullWidth else -fullWidth } + fadeIn() with
                            slideOutHorizontally { fullWidth -> if (targetState == "Nhiệm vụ") -fullWidth else fullWidth } + fadeOut()
                },
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) { currentTab ->
                when (currentTab) {
                    "Ghi chú" -> {
                        SelectableNoteGrid(
                            notes = ghiChuList,
                            isSelectionMode = isNoteSelectionMode,
                            selectedIds = selectedNoteIds,
                            onDetail = { navController.navigate("viewedit/${it.id}") },
                            onEdit = { navController.navigate("viewedit/${it.id}") },
                            onPin = { note ->
                                val pinCount = ghiChuList.count { it.isPinned }
                                if (!note.isPinned && pinCount >= 3) {
                                    Toast.makeText(context, "Chỉ được ghim tối đa 3 ghi chú", Toast.LENGTH_SHORT).show()
                                } else {
                                    ghiChuViewModel.updateGhiChu(note.copy(isPinned = !note.isPinned))
                                }
                            },
                            onDelete = { note ->
                                ghiChuViewModel.deleteGhiChu(note)
                                selectedNoteIds = selectedNoteIds - note.id
                                if (selectedNoteIds.isEmpty()) isNoteSelectionMode = false
                            },
                            onSelectMany = { note ->
                                isNoteSelectionMode = true
                                selectedNoteIds = listOf(note.id)
                            },
                            onSelectChange = { note, checked ->
                                selectedNoteIds = if (checked) selectedNoteIds + note.id else selectedNoteIds - note.id
                                if (selectedNoteIds.isEmpty()) isNoteSelectionMode = false
                            },
                            onDeleteMany = {
                                ghiChuList.filter { selectedNoteIds.contains(it.id) }.forEach { ghiChuViewModel.deleteGhiChu(it) }
                                isNoteSelectionMode = false
                                selectedNoteIds = emptyList()
                            },
                            onCancelMany = {
                                isNoteSelectionMode = false
                                selectedNoteIds = emptyList()
                            }
                        )
                    }
                    "Nhiệm vụ" -> {
                        val showTasks = if (selectedTaskTab == "uncompleted") uncompletedTasks else completedTasks
                        AnimatedContent(
                            targetState = selectedTaskTab,
                            transitionSpec = {
                                slideInVertically { height -> if (targetState == "completed") height else -height } + fadeIn() with
                                        slideOutVertically { height -> if (targetState == "completed") -height else height } + fadeOut()
                            },
                            modifier = Modifier.fillMaxSize()
                        ) { currentSubTab ->
                            SelectableTaskGridWithSubtasks(
                                tasks = if (currentSubTab == "uncompleted") uncompletedTasks else completedTasks,
                                isSelectionMode = isTaskSelectionMode,
                                selectedIds = selectedTaskIds,
                                onDetail = { navController.navigate("viewedittask/${it.id}") },
                                onEdit = { navController.navigate("viewedittask/${it.id}") },
                                onPin = { task ->
                                    val pinCount = nhiemVuList.count { it.isPinned }
                                    if (!task.isPinned && pinCount >= 3) {
                                        Toast.makeText(context, "Chỉ được ghim tối đa 3 nhiệm vụ", Toast.LENGTH_SHORT).show()
                                    } else {
                                        ghiChuViewModel.updateNhiemVu(task.copy(isPinned = !task.isPinned))
                                    }
                                },
                                onDelete = { task ->
                                    ghiChuViewModel.deleteNhiemVu(task)
                                    selectedTaskIds = selectedTaskIds - task.id
                                    if (selectedTaskIds.isEmpty()) isTaskSelectionMode = false
                                },
                                onSelectMany = { task ->
                                    isTaskSelectionMode = true
                                    selectedTaskIds = listOf(task.id)
                                },
                                onSelectChange = { task, checked ->
                                    selectedTaskIds = if (checked) selectedTaskIds + task.id else selectedTaskIds - task.id
                                    if (selectedTaskIds.isEmpty()) isTaskSelectionMode = false
                                },
                                onDeleteMany = {
                                    showTasks.filter { selectedTaskIds.contains(it.id) }.forEach { ghiChuViewModel.deleteNhiemVu(it) }
                                    isTaskSelectionMode = false
                                    selectedTaskIds = emptyList()
                                },
                                onCancelMany = {
                                    isTaskSelectionMode = false
                                    selectedTaskIds = emptyList()
                                },
                                ghiChuViewModel = ghiChuViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectableTaskGridWithSubtasks(
    tasks: List<NhiemVu>,
    isSelectionMode: Boolean,
    selectedIds: List<Int>,
    onDetail: (NhiemVu) -> Unit,
    onEdit: (NhiemVu) -> Unit,
    onPin: (NhiemVu) -> Unit,
    onDelete: (NhiemVu) -> Unit,
    onSelectMany: (NhiemVu) -> Unit,
    onSelectChange: (NhiemVu, Boolean) -> Unit,
    onDeleteMany: () -> Unit,
    onCancelMany: () -> Unit,
    ghiChuViewModel: GhiChuViewModel
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMenuForTaskId by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(tasks) { task ->
                val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val taskDate = try { formatter.parse(task.ngay) } catch (e: Exception) { null }
                val now = Date()
                val isOverdue = taskDate != null && now.after(taskDate) && task.trangThai != "Hoàn thành"

                val targetScale by animateFloatAsState(if (selectedIds.contains(task.id)) 1.02f else 1f, tween(300))
                val cardBackgroundColor by animateColorAsState(if (selectedIds.contains(task.id)) HighlightColor else Color.White, tween(300))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(targetScale)
                        .combinedClickable(
                            onClick = {
                                if (isSelectionMode) {
                                    onSelectChange(task, !selectedIds.contains(task.id))
                                } else {
                                    onDetail(task)
                                }
                            },
                            onLongClick = {
                                if (!isSelectionMode) {
                                    showMenuForTaskId = task.id
                                } else {
                                    onSelectMany(task)
                                }
                            }
                        )
                        .border(
                            width = if (selectedIds.contains(task.id)) 2.dp else 0.dp,
                            color = if (selectedIds.contains(task.id)) IconBackground else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(8.dp)
                ) {
                    Box(Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = (task.trangThai == "Hoàn thành"),
                                    onCheckedChange = { checked ->
                                        val newStatus = if (checked) "Hoàn thành" else "Chưa hoàn thành"
                                        ghiChuViewModel.updateNhiemVu(task.copy(trangThai = newStatus))
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = IconBackground)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column(Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(task.tieuDe, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                                        if (task.isPinned) {
                                            Icon(Icons.Default.PushPin, contentDescription = "Pinned", tint = FabYellow, modifier = Modifier.size(18.dp).padding(start = 4.dp))
                                        }
                                    }
                                    Text(task.trangThai, color = Color.DarkGray, fontSize = 14.sp)
                                    if (isOverdue) {
                                        Text("Đã hết hạn", color = Color.Red, fontSize = 12.sp)
                                    } else {
                                        Text(task.ngay, color = Color.Gray, fontSize = 12.sp)
                                    }
                                }
                                if (isSelectionMode) {
                                    Checkbox(
                                        checked = selectedIds.contains(task.id),
                                        onCheckedChange = { checked -> onSelectChange(task, checked) }
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))

                            var subtasks by remember { mutableStateOf<List<SubTask>>(emptyList()) }
                            LaunchedEffect(task.id) {
                                ghiChuViewModel.getSubTasksForTask(task.id).collectLatest { subtasks = it }
                            }

                            Column(modifier = Modifier.fillMaxWidth().padding(start = 32.dp)) {
                                subtasks.forEach { subtask ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                    ) {
                                        Checkbox(
                                            checked = subtask.isCompleted,
                                            onCheckedChange = { checked ->
                                                ghiChuViewModel.updateSubTask(subtask.copy(isCompleted = checked))
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = IconBackground),
                                            modifier = Modifier.scale(0.9f)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = subtask.title,
                                            fontSize = 14.sp,
                                            color = if (subtask.isCompleted) Color.Gray else Color.Black,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        DropdownMenu(
                            expanded = (showMenuForTaskId == task.id),
                            onDismissRequest = { showMenuForTaskId = null },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            DropdownMenuItem(text = { Text("Xem chi tiết") }, onClick = {
                                showMenuForTaskId = null
                                onDetail(task)
                            })
                            DropdownMenuItem(text = { Text("Sửa") }, onClick = {
                                showMenuForTaskId = null
                                onEdit(task)
                            })
                            DropdownMenuItem(text = { Text(if (task.isPinned) "Bỏ ghim" else "Ghim") }, onClick = {
                                showMenuForTaskId = null
                                onPin(task)
                            })
                            DropdownMenuItem(text = { Text("Xóa") }, onClick = {
                                showMenuForTaskId = null
                                onDelete(task)
                            })
                            DropdownMenuItem(text = { Text("Xóa nhiều") }, onClick = {
                                showMenuForTaskId = null
                                onSelectMany(task)
                            })
                        }
                    }
                }
            }
        }

        if (isSelectionMode && selectedIds.isNotEmpty()) {
            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = IconBackground),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Xóa (${selectedIds.size})", color = ButtonTextColor)
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = { onCancelMany() },
                    colors = ButtonDefaults.buttonColors(containerColor = HighlightColor),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Hủy", color = Color.Black)
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc muốn xóa các nhiệm vụ đã chọn không?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteMany()
                    showDeleteDialog = false
                }) {
                    Text("Xóa", color = IconBackground)
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

// --- Note or Task Card with Menu ---
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteOrTaskCardWithMenu(
    title: String,
    subtitle: String,
    date: String,
    isPinned: Boolean,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onDetail: () -> Unit,
    onEdit: () -> Unit,
    onPin: () -> Unit,
    onDelete: () -> Unit,
    onSelectMany: () -> Unit,
    onSelectChange: (Boolean) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val targetScale by animateFloatAsState(if (isSelected) 1.02f else 1f, tween(300))
    val cardBackgroundColor by animateColorAsState(if (isSelected) HighlightColor else Color.White, tween(300))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .scale(targetScale)
            .combinedClickable(
                onClick = {
                    if (isSelectionMode) onSelectChange(!isSelected) else onDetail()
                },
                onLongClick = { showMenu = true }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            if (isSelectionMode) {
                Checkbox(checked = isSelected, onCheckedChange = { onSelectChange(it) })
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    if (isPinned) Icon(Icons.Default.PushPin, null, tint = FabYellow, modifier = Modifier.size(18.dp).padding(start = 2.dp))
                }
                Spacer(Modifier.height(4.dp))
                Text(subtitle, fontSize = 13.sp, color = Color.DarkGray, maxLines = 2)
                Spacer(modifier = Modifier.weight(1f))
                Text(date, fontSize = 11.sp, color = Color.Gray)
            }
        }
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem({ Text("Xem chi tiết") }, onClick = { showMenu = false; onDetail() })
            DropdownMenuItem({ Text("Sửa") }, onClick = { showMenu = false; onEdit() })
            DropdownMenuItem({ Text(if (isPinned) "Bỏ ghim" else "Ghim") }, onClick = { showMenu = false; onPin() })
            DropdownMenuItem({ Text("Xóa") }, onClick = { showMenu = false; onDelete() })
            DropdownMenuItem({ Text("Xóa nhiều") }, onClick = { showMenu = false; onSelectMany() })
        }
    }
}

// --- SelectableNoteGrid ---
@Composable
fun SelectableNoteGrid(
    notes: List<GhiChu>,
    isSelectionMode: Boolean,
    selectedIds: List<Int>,
    onDetail: (GhiChu) -> Unit,
    onEdit: (GhiChu) -> Unit,
    onPin: (GhiChu) -> Unit,
    onDelete: (GhiChu) -> Unit,
    onSelectMany: (GhiChu) -> Unit,
    onSelectChange: (GhiChu, Boolean) -> Unit,
    onDeleteMany: () -> Unit,
    onCancelMany: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(notes) { note ->
                NoteOrTaskCardWithMenu(
                    title = note.tieuDe,
                    subtitle = note.noiDung,
                    date = note.ngay,
                    isPinned = note.isPinned,
                    isSelectionMode = isSelectionMode,
                    isSelected = selectedIds.contains(note.id),
                    onDetail = { onDetail(note) },
                    onEdit = { onEdit(note) },
                    onPin = { onPin(note) },
                    onDelete = { onDelete(note) },
                    onSelectMany = { onSelectMany(note) },
                    onSelectChange = { checked -> onSelectChange(note, checked) }
                )
            }
        }
        if (isSelectionMode && selectedIds.isNotEmpty()) {
            Row(Modifier.align(Alignment.BottomCenter).padding(16.dp)) {
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = IconBackground),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Xóa (${selectedIds.size})", color = ButtonTextColor)
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = { onCancelMany() },
                    colors = ButtonDefaults.buttonColors(containerColor = HighlightColor),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Hủy", color = Color.Black)
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc muốn xóa các ghi chú đã chọn không?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteMany()
                    showDeleteDialog = false
                }) { Text("Xóa", color = IconBackground) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Hủy") } }
        )
    }
}

// --- Bottom navigation bar ---
@Composable
fun BottomNavigationBar(selectedTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar(containerColor = Color.White) {
        listOf("Ghi chú", "Nhiệm vụ").forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                label = { Text(tab) },
                icon = {
                    when (tab) {
                        "Ghi chú" -> Icon(Icons.Default.List, contentDescription = "Ghi chú")
                        "Nhiệm vụ" -> Icon(Icons.Default.CheckBox, contentDescription = "Nhiệm vụ")
                        else -> Icon(Icons.Default.Help, contentDescription = null)
                    }
                }
            )
        }
    }
}
