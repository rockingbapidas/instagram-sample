package com.example.instagramclone.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.instagramclone.presentation.screens.auth.login.LoginScreen
import com.example.instagramclone.presentation.screens.auth.register.RegisterScreen
import com.example.instagramclone.presentation.screens.create.CreatePostScreen
import com.example.instagramclone.presentation.screens.feed.FeedScreen
import com.example.instagramclone.presentation.screens.notifications.NotificationScreen
import com.example.instagramclone.presentation.screens.profile.ProfileScreen
import com.example.instagramclone.presentation.screens.search.SearchScreen
import com.example.instagramclone.presentation.screens.splash.SplashScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
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
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
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
 