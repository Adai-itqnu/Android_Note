package com.example.qlghichu.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.qlghichu.Entity.NhiemVu
import com.example.qlghichu.MainActivity
import com.example.qlghichu.repository.GhiChuRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

data class TaskNotification(
    val task: NhiemVu,
    val message: String,
    val type: NotificationType,
    val timeRemaining: String
)

enum class NotificationType {
    TEN_MINUTES,
    FIVE_MINUTES,
    ONE_MINUTE,
    EXPIRED
}

// Đổi tên class để tránh conflict với android.app.NotificationManager
object TaskNotificationManager {
    private val _currentNotification = MutableStateFlow<TaskNotification?>(null)
    val currentNotification: StateFlow<TaskNotification?> = _currentNotification

    private val notifiedTasks = mutableSetOf<String>() // taskId + type để tránh thông báo trùng

    fun showNotification(notification: TaskNotification) {
        val key = "${notification.task.id}_${notification.type}"
        if (!notifiedTasks.contains(key)) {
            _currentNotification.value = notification
            notifiedTasks.add(key)
            Log.d("TaskNotificationManager", "Showing notification: ${notification.task.tieuDe} - ${notification.timeRemaining}")
        }
    }

    fun dismissNotification() {
        _currentNotification.value = null
    }

    fun clearNotifiedTasks() {
        notifiedTasks.clear()
    }
}

class NotificationService : Service() {
    private var repository: GhiChuRepository? = null
    private var serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var checkJob: Job? = null
    private val handler = Handler(Looper.getMainLooper())
    private var checkRunnable: Runnable? = null

    companion object {
        private const val FOREGROUND_NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "task_reminder_channel"
        private const val CHANNEL_NAME = "Nhắc nhở nhiệm vụ"
        private const val SERVICE_CHANNEL_ID = "service_channel"
        private const val SERVICE_CHANNEL_NAME = "Dịch vụ chạy ngầm"
        private const val TAG = "NotificationService"

        // Static repository để share giữa service instances
        private var staticRepository: GhiChuRepository? = null

        fun start(context: Context, repository: GhiChuRepository) {
            staticRepository = repository
            val intent = Intent(context, NotificationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        Log.d(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")
        startForeground(FOREGROUND_NOTIFICATION_ID, createServiceNotification())

        // Set repository từ static variable
        repository = staticRepository
        repository?.let {
            Log.d(TAG, "Repository set, starting notification check")
            startNotificationCheck()
        } ?: Log.e(TAG, "Repository is null!")

        return START_STICKY
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Explicit cast để tránh conflict với class tự định nghĩa
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

            // Channel cho service foreground
            val serviceChannel = NotificationChannel(
                SERVICE_CHANNEL_ID,
                SERVICE_CHANNEL_NAME,
                android.app.NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Kênh cho dịch vụ chạy ngầm"
                setShowBadge(false)
            }

            // Channel cho thông báo nhiệm vụ
            val taskChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Kênh thông báo nhắc nhở nhiệm vụ"
                enableLights(true)
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(serviceChannel)
            notificationManager.createNotificationChannel(taskChannel)
        }
    }

    private fun createServiceNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, SERVICE_CHANNEL_ID)
            .setContentTitle("Dịch vụ nhắc nhở đang hoạt động")
            .setContentText("Ứng dụng đang theo dõi các nhiệm vụ của bạn")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    private fun startNotificationCheck() {
        checkJob?.cancel() // Cancel previous job if exists

        checkRunnable = object : Runnable {
            override fun run() {
                serviceScope.launch {
                    repository?.let { repo ->
                        checkTaskDeadlines(repo)
                    }
                }
                handler.postDelayed(this, 30000) // Check every 30 seconds
            }
        }

        handler.post(checkRunnable!!)
        Log.d(TAG, "Notification check started")
    }

    private suspend fun checkTaskDeadlines(repository: GhiChuRepository) {
        try {
            // Get all active tasks
            val tasks = repository.getAllNhiemVu("").first()
            val currentTime = System.currentTimeMillis()
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            Log.d(TAG, "Checking ${tasks.size} tasks for notifications")

            tasks.filter { !it.isDeleted && it.trangThai != "Hoàn thành" }.forEach { task ->
                try {
                    val taskTime = sdf.parse(task.ngay)?.time ?: return@forEach
                    val timeDiff = taskTime - currentTime

                    Log.d(TAG, "Task: ${task.tieuDe}, Time diff: ${timeDiff}ms (${timeDiff/1000/60} minutes)")

                    when {
                        timeDiff <= 0 && timeDiff > -300000 -> { // Đã hết hạn (trong vòng 5 phút gần đây)
                            val notification = TaskNotification(
                                task = task,
                                message = "Nhiệm vụ đã hết hạn!",
                                type = NotificationType.EXPIRED,
                                timeRemaining = "Đã hết hạn"
                            )
                            showSystemNotification(notification)
                            TaskNotificationManager.showNotification(notification)
                        }
                        timeDiff in 1..60000 -> { // Còn 1 phút
                            val notification = TaskNotification(
                                task = task,
                                message = "Nhiệm vụ sắp hết hạn!",
                                type = NotificationType.ONE_MINUTE,
                                timeRemaining = "Còn 1 phút"
                            )
                            showSystemNotification(notification)
                            TaskNotificationManager.showNotification(notification)
                        }
                        timeDiff in 60001..300000 -> { // Còn 5 phút
                            val notification = TaskNotification(
                                task = task,
                                message = "Nhiệm vụ sắp hết hạn!",
                                type = NotificationType.FIVE_MINUTES,
                                timeRemaining = "Còn 5 phút"
                            )
                            showSystemNotification(notification)
                            TaskNotificationManager.showNotification(notification)
                        }
                        timeDiff in 300001..600000 -> { // Còn 10 phút
                            val notification = TaskNotification(
                                task = task,
                                message = "Nhiệm vụ sắp hết hạn!",
                                type = NotificationType.TEN_MINUTES,
                                timeRemaining = "Còn 10 phút"
                            )
                            showSystemNotification(notification)
                            TaskNotificationManager.showNotification(notification)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing date for task: ${task.tieuDe}", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking task deadlines", e)
        }
    }

    private fun showSystemNotification(taskNotification: TaskNotification) {
        val key = "${taskNotification.task.id}_${taskNotification.type}"
        val notifiedTasks = getSharedPreferences("notifications", Context.MODE_PRIVATE)

        // Kiểm tra xem đã thông báo chưa
        if (notifiedTasks.getBoolean(key, false)) {
            Log.d(TAG, "Already notified for: $key")
            return
        }

        // Đánh dấu đã thông báo
        notifiedTasks.edit().putBoolean(key, true).apply()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("taskId", taskNotification.task.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            taskNotification.task.id,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val (priority, color) = when (taskNotification.type) {
            NotificationType.EXPIRED -> Pair(NotificationCompat.PRIORITY_MAX, 0xFFF44336)
            NotificationType.ONE_MINUTE -> Pair(NotificationCompat.PRIORITY_HIGH, 0xFFFF5722)
            NotificationType.FIVE_MINUTES -> Pair(NotificationCompat.PRIORITY_DEFAULT, 0xFFFF9800)
            NotificationType.TEN_MINUTES -> Pair(NotificationCompat.PRIORITY_DEFAULT, 0xFF4CAF50)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("${taskNotification.timeRemaining} - ${taskNotification.task.tieuDe}")
            .setContentText("${taskNotification.message}\nHạn: ${taskNotification.task.ngay}")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setColor(color.toInt())
            .setPriority(priority)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("${taskNotification.message}\n\nNhiệm vụ: ${taskNotification.task.tieuDe}\nHạn: ${taskNotification.task.ngay}"))
            .build()

        try {
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(taskNotification.task.id + 1000, notification)
            Log.d(TAG, "System notification shown for: ${taskNotification.task.tieuDe}")
        } catch (e: SecurityException) {
            Log.e(TAG, "Notification permission not granted", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        checkJob?.cancel()
        checkRunnable?.let { handler.removeCallbacks(it) }
        serviceScope.cancel()
        Log.d(TAG, "Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}