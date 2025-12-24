package com.example.instagramclone.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagramclone.domain.usecase.auth.IsAuthenticatedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val isAuthenticatedUseCase: IsAuthenticatedUseCase
) : ViewModel() {

    private val _navigationState = MutableStateFlow<SplashNavigationState>(SplashNavigationState.Loading)
    val navigationState: StateFlow<SplashNavigationState> = _navigationState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            // Small delay for splash screen effect
            delay(1000)
            
            isAuthenticatedUseCase().collect { isAuthenticated ->
                _navigationState.value = if (isAuthenticated) {
                    SplashNavigationState.NavigateToFeed
                } else {
                    SplashNavigationState.NavigateToLogin
                }
            }
        }
    }
}

sealed class SplashNavigationState {
    data object Loading : SplashNavigationState()
    data object NavigateToLogin : SplashNavigationState()
    data object NavigateToFeed : SplashNavigationState()
}
