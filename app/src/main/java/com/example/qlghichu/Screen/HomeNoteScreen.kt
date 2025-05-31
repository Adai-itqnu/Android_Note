package com.example.qlghichu.Screen


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
            FloatingActionButton(
                onClick = {
                    when (selectedTab) {
                        "Ghi chú" -> {
                            navViewModel.setSelectedTab("Ghi chú")
                            navController.navigate("create")
                        }
                        "Nhiệm vụ" -> {
                            navViewModel.setSelectedTab("Nhiệm vụ")
                            navController.navigate("createtask")
                        }
                    }
                },
                containerColor = Color(0xFFFFC107),
                shape = RoundedCornerShape(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Thêm",
                    tint = Color.White
                )
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

            when (selectedTab) {
                "Ghi chú" -> NoteGrid(ghiChuList, navController, ghiChuViewModel)
                "Nhiệm vụ" -> TaskGrid(nhiemVuList, ghiChuViewModel)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteGrid(
    ghiChuList: List<GhiChu>,
    navController: NavController,
    viewModel: GhiChuViewModel
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<GhiChu?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(ghiChuList) { note ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .combinedClickable(
                        onClick = {
                            // Nhấn để xem/sửa ghi chú
                            navController.navigate("viewedit/${note.id}")
                        },
                        onLongClick = {
                            // Giữ để xóa ghi chú
                            noteToDelete = note
                            showDeleteDialog = true
                        }
                    )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(note.tieuDe, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(note.noiDung, fontSize = 13.sp, color = Color.DarkGray, maxLines = 2)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(note.ngay, fontSize = 11.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End))
                }
            }
        }
    }

    // Dialog xác nhận xóa
    if (showDeleteDialog && noteToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                noteToDelete = null
            },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa ghi chú \"${noteToDelete?.tieuDe}\" không?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        noteToDelete?.let {
                            viewModel.deleteGhiChu(it)
                        }
                        showDeleteDialog = false
                        noteToDelete = null
                    }
                ) {
                    Text("Xóa", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        noteToDelete = null
                    }
                ) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
private fun TaskGrid(nhiemVuList: List<NhiemVu>, viewModel: GhiChuViewModel) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(nhiemVuList) { task ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = task.trangThai == "Hoàn thành",
                        onCheckedChange = { isChecked ->
                            val newStatus = if (isChecked) "Hoàn thành" else "Chưa hoàn thành"
                            viewModel.updateNhiemVu(task.copy(trangThai = newStatus))
                        }
                    )
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