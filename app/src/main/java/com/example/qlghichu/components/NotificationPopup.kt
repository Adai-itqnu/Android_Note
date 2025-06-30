package com.example.qlghichu.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qlghichu.service.TaskNotificationManager
import com.example.qlghichu.service.NotificationType
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NotificationPopup() {
    val notification by TaskNotificationManager.currentNotification.collectAsState()

    AnimatedVisibility(
        visible = notification != null,
        enter = slideInVertically(
            initialOffsetY = { -it }
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it }
        ) + fadeOut()
    ) {
        notification?.let { notif ->
            val backgroundColor = when (notif.type) {
                NotificationType.EXPIRED -> Color(0xFFF44336)
                NotificationType.ONE_MINUTE -> Color(0xFFFF5722)
                NotificationType.FIVE_MINUTES -> Color(0xFFFF9800)
                NotificationType.TEN_MINUTES -> Color(0xFF4CAF50)
            }

            // Auto dismiss after 5 seconds
            LaunchedEffect(notif) {
                delay(5000)
                TaskNotificationManager.dismissNotification()
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(8.dp, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = backgroundColor
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = notif.timeRemaining,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = notif.task.tieuDe,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Hạn: ${notif.task.ngay}",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = { TaskNotificationManager.dismissNotification() }
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}