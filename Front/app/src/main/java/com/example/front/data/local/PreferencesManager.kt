package com.example.front.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_ROLE = "role"
        private const val KEY_IS_GUEST = "is_guest"
    }
    
    fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }
    
    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }
    
    fun saveUser(userId: Long, email: String, role: String) {
        sharedPreferences.edit().apply {
            putLong(KEY_USER_ID, userId)
            putString(KEY_EMAIL, email)
            putString(KEY_ROLE, role)
            putBoolean(KEY_IS_GUEST, false)
            apply()
        }
    }
    
    fun setGuestMode(isGuest: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_GUEST, isGuest)
            .apply()
    }
    
    fun isGuestMode(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_GUEST, false)
    }
    
    fun getUserId(): Long {
        return sharedPreferences.getLong(KEY_USER_ID, -1L)
    }
    
    fun getEmail(): String? {
        return sharedPreferences.getString(KEY_EMAIL, null)
    }
    
    fun getRole(): String? {
        return sharedPreferences.getString(KEY_ROLE, null)
    }
    
    fun clearAuth() {
        sharedPreferences.edit().clear().apply()
    }
    
    fun isLoggedIn(): Boolean {
        return getToken() != null || isGuestMode()
    }
    
    fun isAuthenticated(): Boolean {
        return getToken() != null && !isGuestMode()
    }
}
