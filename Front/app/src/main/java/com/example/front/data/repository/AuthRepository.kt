package com.example.front.data.repository

import com.example.front.data.api.ApiService
import com.example.front.data.local.PreferencesManager
import com.example.front.data.model.LoginRequest
import com.example.front.data.model.LoginResponse
import com.example.front.data.model.RegisterRequest
import com.example.front.data.model.User
import com.example.front.util.Resource

class AuthRepository(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) {
    suspend fun login(email: String, password: String): Resource<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            
            // Add logging for debugging
            android.util.Log.d("AuthRepository", "Login response received: userId=${response.userId}, employeeId=${response.employeeId}")
            
            preferencesManager.saveToken(response.token)
            preferencesManager.saveUser(response.userId, response.email, response.role, response.employeeId)
            
            // Verify data was saved correctly
            val savedEmployeeId = preferencesManager.getEmployeeId()
            android.util.Log.d("AuthRepository", "Saved to SharedPreferences: employeeId=$savedEmployeeId")
            
            if (response.employeeId == null) {
                android.util.Log.w("AuthRepository", "User logged in without employee record. User ID: ${response.userId}")
            }
            
            Resource.Success(response)
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Login error", e)
            Resource.Error(e.message ?: "Ошибка входа")
        }
    }
    
    suspend fun register(email: String, password: String, role: String): Resource<User> {
        return try {
            val response = apiService.register(RegisterRequest(email, password, role))
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка регистрации")
        }
    }
    
    suspend fun getCurrentUser(): Resource<User> {
        return try {
            val response = apiService.getCurrentUser()
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка получения данных пользователя")
        }
    }
    
    fun logout() {
        preferencesManager.clearAuth()
    }
    
    fun loginAsGuest() {
        preferencesManager.setGuestMode(true)
    }
    
    fun isLoggedIn(): Boolean {
        return preferencesManager.isLoggedIn()
    }
    
    fun isGuestMode(): Boolean {
        return preferencesManager.isGuestMode()
    }
    
    fun isAuthenticated(): Boolean {
        return preferencesManager.isAuthenticated()
    }
}
