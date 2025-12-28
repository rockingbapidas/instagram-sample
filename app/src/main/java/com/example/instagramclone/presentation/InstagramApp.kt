package com.example.instagramclone.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.instagramclone.presentation.navigation.NavGraph
import com.example.instagramclone.presentation.navigation.Screen

@Composable
fun InstagramApp(startDestination: String = Screen.Splash.route) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Hide bottom bar on auth screens
    val showBottomBar = currentRoute !in listOf(
        Screen.Splash.route,
        Screen.Login.route,
        Screen.Register.route
    )
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { paddingValues ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
            startDestination = startDestination
        )
    }
}

@Composable
private fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Feed,
        Screen.Search,
        Screen.CreatePost,
        Screen.Notifications,
        Screen.Profile
    )
    
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = when (screen) {
                            Screen.Feed -> Icons.Default.Home
                            Screen.Search -> Icons.Default.Search
                            Screen.CreatePost -> Icons.Default.Add
                            Screen.Notifications -> Icons.Default.Notifications
                            Screen.Profile -> Icons.Default.Person
                            Screen.Splash, Screen.Login, Screen.Register -> Icons.Default.Home // Not used in bottom bar
                        },
                        contentDescription = screen.route
                    )
                },
                label = { Text(screen.route) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}
