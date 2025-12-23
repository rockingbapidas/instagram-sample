package com.example.instagramclone.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.instagramclone.presentation.screens.feed.FeedScreen
import com.example.instagramclone.presentation.screens.profile.ProfileScreen
import com.example.instagramclone.presentation.screens.search.SearchScreen
import com.example.instagramclone.presentation.screens.create.CreatePostScreen
import com.example.instagramclone.presentation.screens.notifications.NotificationScreen
import androidx.compose.ui.Modifier

sealed class Screen(val route: String) {
    object Feed : Screen("feed")
    object Search : Screen("search")
    object CreatePost : Screen("create_post")
    object Profile : Screen("profile")
    object Notifications : Screen("notifications")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Feed.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Feed.route) {
            FeedScreen(navController)
        }
        composable(Screen.Search.route) {
            SearchScreen(navController)
        }
        composable(Screen.CreatePost.route) {
            CreatePostScreen(navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
        composable(Screen.Notifications.route) {
            NotificationScreen()
        }
    }
} 