package com.example.front.ui.departments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.front.data.api.RetrofitClient
import com.example.front.data.local.PreferencesManager
import com.example.front.data.repository.DepartmentRepository
import com.example.front.data.repository.EmployeeRepository

class DepartmentViewModelFactory(
    private val preferencesManager: PreferencesManager
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DepartmentViewModel::class.java)) {
            val apiService = RetrofitClient.getApiService { preferencesManager.getToken() }
            val departmentRepository = DepartmentRepository(apiService)
            val employeeRepository = EmployeeRepository(apiService)
            @Suppress("UNCHECKED_CAST")
            return DepartmentViewModel(departmentRepository, employeeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
