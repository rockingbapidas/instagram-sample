package com.example.instagramclone.presentation.screens.splash

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val navigationState by viewModel.navigationState.collectAsState()

    // Handle navigation based on auth state
    LaunchedEffect(navigationState) {
        when (navigationState) {
            is SplashNavigationState.NavigateToLogin -> {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            is SplashNavigationState.NavigateToFeed -> {
                navController.navigate("feed") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            SplashNavigationState.Loading -> {
                // Stay on splash screen
            }
        }
    }

    // Splash screen UI
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Instagram",
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            CircularProgressIndicator()
        }
    }
}
