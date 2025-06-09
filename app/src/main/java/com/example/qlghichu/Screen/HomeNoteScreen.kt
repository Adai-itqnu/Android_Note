package com.example.qlghichu.Screen

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.qlghichu.Entity.GhiChu
import com.example.qlghichu.Entity.NhiemVu
import com.example.qlghichu.ViewModel.GhiChuViewModel
import com.example.qlghichu.ViewModel.NavigationViewModel
import kotlinx.coroutines.flow.collectLatest

val HighlightColor = Color(0xFFFFE4D1) // Cam nhạt nền chọn
val IconBackground = Color(0xFF3A2513)  // Nâu đậm (check, viền chọn, nút xóa)
val ButtonTextColor = Color.White
val FabYellow = Color(0xFFFFC107)

@Composable
fun TaskTabBar(
    selected: String,
    onSelected: (String) -> Unit,
    fontSize: Int = 16
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 0.dp, start = 12.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        TaskTab(
            text = "Chưa hoàn thành",
            selected = selected == "uncompleted",
            onClick = { onSelected("uncompleted") },
            fontSize = fontSize
        )
        Spacer(modifier = Modifier.width(24.dp))
        TaskTab(
            text = "Đã hoàn thành",
            selected = selected == "completed",
            onClick = { onSelected("completed") },
            fontSize = fontSize
        )
    }
}

@Composable
fun TaskTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    fontSize: Int = 16
) {
    Column(
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) IconBackground else Color.Gray,
            modifier = Modifier
                .padding(horizontal = 6.dp, vertical = 4.dp)
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .height(4.dp)
                    .width(32.dp)
                    .background(Color(0xFFFFE4D1), RoundedCornerShape(8.dp))
            )
        } else {
            Spacer(Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeNoteScreen(
    navController: NavController,
    ghiChuViewModel: GhiChuViewModel = viewModel(),
    navViewModel: NavigationViewModel = viewModel()
) {
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
            ghiChuList = list.sortedByDescending { it.id }
        }
    }
    LaunchedEffect(searchNhiemVu) {
        ghiChuViewModel.getAllNhiemVu(searchNhiemVu).collectLatest { list ->
            nhiemVuList = list.sortedByDescending { it.id }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Ghi Chú",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(start = 16.dp, top = 30.dp)
                    )
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { navViewModel.setSelectedTab(it) }
            )
        },
        floatingActionButton = {
            if (!isNoteSelectionMode && !isTaskSelectionMode) {
                FloatingActionButton(
                    onClick = {
                        when (selectedTab) {
                            "Ghi chú" -> navController.navigate("create")
                            "Nhiệm vụ" -> navController.navigate("createtask")
                        }
                    },
                    containerColor = FabYellow,
                    shape = RoundedCornerShape(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Thêm",
                        tint = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = if (selectedTab == "Ghi chú") searchGhiChu else searchNhiemVu,
                onValueChange = {
                    if (selectedTab == "Ghi chú") searchGhiChu = it
                    else searchNhiemVu = it
                },
                textStyle = TextStyle(fontSize = 16.sp),
                placeholder = {
                    Text(
                        if (selectedTab == "Ghi chú") "Tìm Kiếm Ghi chú" else "Tìm Kiếm Nhiệm vụ",
                        fontSize = 16.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = Color.Gray
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(52.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 30.dp, end = 30.dp)
                    .height(52.dp)
            )

            if (selectedTab == "Nhiệm vụ") {
                TaskTabBar(selectedTaskTab, { selectedTaskTab = it }, fontSize = 16)
            }

            // Dùng Box(modifier = Modifier.weight(1f)) để danh sách chiếm toàn bộ phần còn lại
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    "Ghi chú" -> {
                        SelectableNoteGrid(
                            notes = ghiChuList,
                            isSelectionMode = isNoteSelectionMode,
                            selectedIds = selectedNoteIds,
                            onItemClick = { note ->
                                navController.navigate("viewedit/${note.id}")
                            },
                            onItemLongClick = { note ->
                                isNoteSelectionMode = true
                                selectedNoteIds = listOf(note.id)
                            },
                            onCheckboxChange = { note, checked ->
                                selectedNoteIds = if (checked) selectedNoteIds + note.id else selectedNoteIds - note.id
                                if (selectedNoteIds.isEmpty()) isNoteSelectionMode = false
                            },
                            onDelete = {
                                ghiChuList.filter { selectedNoteIds.contains(it.id) }
                                    .forEach { ghiChuViewModel.deleteGhiChu(it) }
                                selectedNoteIds = emptyList()
                                isNoteSelectionMode = false
                            },
                            onCancel = {
                                isNoteSelectionMode = false
                                selectedNoteIds = emptyList()
                            }
                        )
                    }
                    "Nhiệm vụ" -> {
                        val showTasks = if (selectedTaskTab == "uncompleted") uncompletedTasks else completedTasks
                        SelectableTaskGrid(
                            tasks = showTasks,
                            isSelectionMode = isTaskSelectionMode,
                            selectedIds = selectedTaskIds,
                            onItemClick = { task ->
                                navController.navigate("viewedittask/${task.id}")
                            },
                            onItemLongClick = { task ->
                                isTaskSelectionMode = true
                                selectedTaskIds = listOf(task.id)
                            },
                            onCheckboxChange = { task, checked ->
                                selectedTaskIds = if (checked) selectedTaskIds + task.id else selectedTaskIds - task.id
                                if (selectedTaskIds.isEmpty()) isTaskSelectionMode = false
                            },
                            onStatusChange = { task, checked ->
                                val newStatus = if (checked) "Hoàn thành" else "Chưa hoàn thành"
                                ghiChuViewModel.updateNhiemVu(task.copy(trangThai = newStatus))
                            },
                            onDelete = {
                                showTasks.filter { selectedTaskIds.contains(it.id) }
                                    .forEach { ghiChuViewModel.deleteNhiemVu(it) }
                                selectedTaskIds = emptyList()
                                isTaskSelectionMode = false
                            },
                            onCancel = {
                                isTaskSelectionMode = false
                                selectedTaskIds = emptyList()
                            }
                        )
                    }
                }
            }
        }
    }
}

// --- Note Grid ---
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectableNoteGrid(
    notes: List<GhiChu>,
    isSelectionMode: Boolean,
    selectedIds: List<Int>,
    onItemClick: (GhiChu) -> Unit,
    onItemLongClick: (GhiChu) -> Unit,
    onCheckboxChange: (GhiChu, Boolean) -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit
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
                val isSelected = selectedIds.contains(note.id)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) HighlightColor else Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .combinedClickable(
                            onClick = {
                                if (isSelectionMode) {
                                    onCheckboxChange(note, !isSelected)
                                } else {
                                    onItemClick(note)
                                }
                            },
                            onLongClick = { onItemLongClick(note) }
                        )
                        .border(
                            width = if (isSelected) 2.dp else 0.dp,
                            color = if (isSelected) IconBackground else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        if (isSelectionMode) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked -> onCheckboxChange(note, checked) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = IconBackground
                                )
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(note.tieuDe, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            Spacer(Modifier.height(4.dp))
                            Text(note.noiDung, fontSize = 13.sp, color = Color.DarkGray, maxLines = 2)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(note.ngay, fontSize = 11.sp, color = Color.Gray)
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
                    onClick = { onCancel() },
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
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Xóa", color = IconBackground)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Hủy") }
            }
        )
    }
}

// --- Task Grid ---
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectableTaskGrid(
    tasks: List<NhiemVu>,
    isSelectionMode: Boolean,
    selectedIds: List<Int>,
    onItemClick: (NhiemVu) -> Unit,
    onItemLongClick: (NhiemVu) -> Unit,
    onCheckboxChange: (NhiemVu, Boolean) -> Unit,
    onStatusChange: (NhiemVu, Boolean) -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit
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
            items(tasks) { task ->
                val isSelected = selectedIds.contains(task.id)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) HighlightColor else Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .combinedClickable(
                            onClick = {
                                if (isSelectionMode) {
                                    onCheckboxChange(task, !isSelected)
                                } else {
                                    onItemClick(task)
                                }
                            },
                            onLongClick = { onItemLongClick(task) }
                        )
                        .border(
                            width = if (isSelected) 2.dp else 0.dp,
                            color = if (isSelected) IconBackground else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSelectionMode) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked -> onCheckboxChange(task, checked) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = IconBackground
                                )
                            )
                        } else {
                            Checkbox(
                                checked = task.trangThai == "Hoàn thành",
                                onCheckedChange = { checked -> onStatusChange(task, checked) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = IconBackground
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.fillMaxHeight()) {
                            Text(task.tieuDe, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(task.trangThai, fontSize = 13.sp, color = Color.DarkGray, maxLines = 2)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(task.ngay, fontSize = 11.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End))
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
                    onClick = { onCancel() },
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
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Xóa", color = IconBackground)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Hủy") }
            }
        )
    }
}

@Composable
fun BottomNavigationBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
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
                    }
                }
            )
        }
    }
}
