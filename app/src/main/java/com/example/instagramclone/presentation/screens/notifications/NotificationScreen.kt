package com.example.instagramclone.presentation.screens.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.instagramclone.domain.model.Notification
import com.example.instagramclone.domain.model.NotificationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()

    NotificationScreenContent(
        notifications = notifications,
        unreadCount = unreadCount,
        onMarkAllAsRead = viewModel::markAllAsRead,
        onNotificationClick = viewModel::handleNotificationClick,
        onDeleteNotification = viewModel::deleteNotification,
        onMarkAsRead = viewModel::markAsRead
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreenContent(
    notifications: List<Notification>,
    unreadCount: Int,
    onMarkAllAsRead: () -> Unit,
    onNotificationClick: (Notification) -> Unit,
    onDeleteNotification: (String) -> Unit,
    onMarkAsRead: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                actions = {
                    if (unreadCount > 0) {
                        TextButton(onClick = onMarkAllAsRead) {
                            Text("Mark all read")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No notifications yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications) { notification ->
                    NotificationItem(
                        notification = notification,
                        onNotificationClick = { onNotificationClick(notification) },
                        onDelete = { onDeleteNotification(notification.id) },
                        onMarkAsRead = { onMarkAsRead(notification.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationItem(
    notification: Notification,
    onNotificationClick: () -> Unit,
    onDelete: () -> Unit,
    onMarkAsRead: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            onNotificationClick()
            if (!notification.isRead) {
                onMarkAsRead()
            }
        },
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(notification.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete notification",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> "$days day${if (days > 1) "s" else ""} ago"
        hours > 0 -> "$hours hour${if (hours > 1) "s" else ""} ago"
        minutes > 0 -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
        else -> "Just now"
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    val dummyNotifications = listOf(
        Notification(
            id = "1",
            type = NotificationType.LIKE,
            title = "New Like",
            message = "John Doe liked your post.",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 5, // 5 minutes ago
            isRead = false
        ),
        Notification(
            id = "2",
            type = NotificationType.COMMENT,
            title = "New Comment",
            message = "Jane Smith commented on your photo.",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2, // 2 hours ago
            isRead = true
        ),
        Notification(
            id = "3",
            type = NotificationType.FOLLOW,
            title = "New Follower",
            message = "Bob Johnson started following you.",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24, // 1 day ago
            isRead = false
        )
    )

    MaterialTheme {
        NotificationScreenContent(
            notifications = dummyNotifications,
            unreadCount = 2,
            onMarkAllAsRead = {},
            onNotificationClick = {},
            onDeleteNotification = {},
            onMarkAsRead = {}
        )
    }
}
