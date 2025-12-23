package com.example.instagramclone.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.instagramclone.presentation.navigation.Screen
import com.example.instagramclone.presentation.theme.InstagramCloneTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val startDestination = getStartDestination(intent)
        
        setContent {
            InstagramCloneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    InstagramApp(startDestination = startDestination)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.let {
            // Handle notification default intent if needed
            val notificationId = it.getStringExtra("notificationId")
            val notificationType = it.getStringExtra("notificationType")
            // You can navigate to specific screen based on notification type
        }
    }

    private fun getStartDestination(intent: Intent?): String {
        return when {
            intent?.getStringExtra("navigateTo") == "post" -> {
                val postId = intent.getStringExtra("postId")
                // Navigate to post detail screen
                Screen.Feed.route
            }
            intent?.getStringExtra("navigateTo") == "notifications" -> {
                Screen.Notifications.route
            }
            else -> Screen.Feed.route
        }
    }
}
