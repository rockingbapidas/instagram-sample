package com.example.instagramclone.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class FeedPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun saveNextCursor(cursor: String?) {
        sharedPreferences.edit { 
            if (cursor != null) {
                putString(KEY_NEXT_CURSOR, cursor)
            } else {
                remove(KEY_NEXT_CURSOR)
            }
        }
    }

    fun getNextCursor(): String? {
        return sharedPreferences.getString(KEY_NEXT_CURSOR, null)
    }

    fun clearCursor() {
        sharedPreferences.edit { remove(KEY_NEXT_CURSOR) }
    }

    companion object {
        private const val PREFS_NAME = "instagram_feed_prefs"
        private const val KEY_NEXT_CURSOR = "next_cursor"
    }
}
