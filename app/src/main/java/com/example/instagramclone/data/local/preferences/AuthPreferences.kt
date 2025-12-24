package com.example.instagramclone.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class AuthPreferences @Inject constructor(
    context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        /* context = */ context,
        /* fileName = */ PREFS_NAME,
        /* masterKey = */ masterKey,
        /* prefKeyEncryptionScheme = */ EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        /* prefValueEncryptionScheme = */ EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _authTokenFlow = MutableStateFlow(getAuthToken())
    val authTokenFlow: Flow<String?> = _authTokenFlow.asStateFlow()

    fun saveAuthToken(token: String) {
        sharedPreferences.edit { putString(KEY_AUTH_TOKEN, token) }
        _authTokenFlow.value = token
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    fun saveUserId(userId: String) {
        sharedPreferences.edit { putString(KEY_USER_ID, userId) }
    }

    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    fun clearAuthData() {
        sharedPreferences.edit { clear() }
        _authTokenFlow.value = null
    }

    fun isAuthenticated(): Boolean {
        return getAuthToken() != null
    }

    companion object {
        private const val PREFS_NAME = "instagram_auth_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
    }
}
