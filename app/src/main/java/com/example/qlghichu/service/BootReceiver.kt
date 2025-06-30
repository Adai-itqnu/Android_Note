package com.example.qlghichu.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.qlghichu.database.DatabaseProvider
import com.example.qlghichu.repository.GhiChuRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "Boot receiver called with action: ${intent?.action}")

        if (intent?.action == Intent.ACTION_BOOT_COMPLETED ||
            intent?.action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            intent?.action == Intent.ACTION_PACKAGE_REPLACED) {

            context?.let { ctx ->
                Log.d(TAG, "Starting service after boot/update")

                // Initialize repository và start service
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = DatabaseProvider.getDatabase(ctx)
                        val repository = GhiChuRepository(
                            db.ghiChuDao(),
                            db.nhiemVuDao(),
                            db.subTaskDao()
                        )

                        // Start service with repository
                        NotificationService.start(ctx, repository)
                        Log.d(TAG, "Service started successfully")

                    } catch (e: Exception) {
                        Log.e(TAG, "Error initializing service", e)
                    }
                }
            }
        }
    }
}