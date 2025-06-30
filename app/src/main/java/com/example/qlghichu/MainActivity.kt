package com.example.qlghichu

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qlghichu.Routes.SetupNavGraph
import com.example.qlghichu.ViewModel.GhiChuViewModel
import com.example.qlghichu.components.NotificationPopup
import com.example.qlghichu.database.DatabaseProvider
import com.example.qlghichu.repository.GhiChuRepository
import com.example.qlghichu.service.NotificationService
import com.example.qlghichu.ui.theme.QLGhiChuTheme

class MainActivity : ComponentActivity() {
    // Request notification permission for Android 13+
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startNotificationService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startNotificationService()
        }

        setContent {
            QLGhiChuTheme {
                val viewModel: GhiChuViewModel = viewModel()

                // Initialize repository and start service
                LaunchedEffect(Unit) {
                    initializeAndStartService()
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    SetupNavGraph()
                    // Notification popup overlay - always on top
                    NotificationPopup()
                }
            }
        }
    }

    private fun initializeAndStartService() {
        val db = DatabaseProvider.getDatabase(application)
        val repository = GhiChuRepository(
            db.ghiChuDao(),
            db.nhiemVuDao(),
            db.subTaskDao()
        )

        // Start service with repository
        NotificationService.start(this, repository)
    }

    private fun startNotificationService() {
        val serviceIntent = Intent(this, NotificationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Ensure service is running when app resumes
        startNotificationService()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Service sẽ tiếp tục chạy ngay cả khi Activity bị destroy
        // Đây là điều chúng ta muốn để nhận thông báo ngầm
    }
}