package com.example.qlghichu.Routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.qlghichu.Screen.CreateNoteScreen
import com.example.qlghichu.Screen.CreateTaskScreen
import com.example.qlghichu.Screen.HomeNoteScreen
import com.example.qlghichu.Screen.ViewEditNoteScreen
import com.example.qlghichu.Screen.ViewEditTaskScreen
import com.example.qlghichu.Screen.TrashGhiChuScreen
import com.example.qlghichu.Screen.TrashNhiemVuScreen

@Composable
fun SetupNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeNoteScreen(navController) }
        composable("create") { CreateNoteScreen(navController) }
        composable("createtask") { CreateTaskScreen(navController) }
        composable(
            "viewedit/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0
            ViewEditNoteScreen(navController, noteId)
        }
        composable(
            "viewedittask/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0
            ViewEditTaskScreen(navController, taskId)
        }
        // Thùng rác cho Ghi chú
        composable("trash/ghi_chu") {
            TrashGhiChuScreen(navController = navController)
        }
        // Thùng rác cho Nhiệm vụ
        composable("trash/nhiem_vu") {
            TrashNhiemVuScreen(navController = navController)
        }
    }
}
