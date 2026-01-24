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
            preferencesManager.saveToken(response.token)
            preferencesManager.saveUser(response.userId, response.email, response.role, response.employeeId)
            Resource.Success(response)
        } catch (e: Exception) {
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
