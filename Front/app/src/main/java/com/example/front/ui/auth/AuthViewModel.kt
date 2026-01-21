package com.example.front.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.data.model.LoginResponse
import com.example.front.data.model.User
import com.example.front.data.repository.AuthRepository
import com.example.front.util.Resource
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    
    private val _loginResult = MutableLiveData<Resource<LoginResponse>>()
    val loginResult: LiveData<Resource<LoginResponse>> = _loginResult
    
    private val _registerResult = MutableLiveData<Resource<User>>()
    val registerResult: LiveData<Resource<User>> = _registerResult
    
    private val _guestLoginResult = MutableLiveData<Boolean>()
    val guestLoginResult: LiveData<Boolean> = _guestLoginResult
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = Resource.Loading()
            _loginResult.value = repository.login(email, password)
        }
    }
    
    fun register(email: String, password: String, role: String) {
        viewModelScope.launch {
            _registerResult.value = Resource.Loading()
            _registerResult.value = repository.register(email, password, role)
        }
    }
    
    fun isLoggedIn(): Boolean = repository.isLoggedIn()
    
    fun loginAsGuest() {
        repository.loginAsGuest()
        _guestLoginResult.value = true
    }
    
    fun isGuestMode(): Boolean {
        return repository.isGuestMode()
    }
    
    fun isAuthenticated(): Boolean {
        return repository.isAuthenticated()
    }
    
    fun logout() {
        repository.logout()
    }
}
