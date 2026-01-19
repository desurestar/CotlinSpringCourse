package com.example.front.ui.departments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.front.data.api.RetrofitClient
import com.example.front.data.local.PreferencesManager
import com.example.front.data.repository.DepartmentRepository

class DepartmentViewModelFactory(
    private val preferencesManager: PreferencesManager
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DepartmentViewModel::class.java)) {
            val apiService = RetrofitClient.getApiService { preferencesManager.getToken() }
            val repository = DepartmentRepository(apiService)
            @Suppress("UNCHECKED_CAST")
            return DepartmentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
