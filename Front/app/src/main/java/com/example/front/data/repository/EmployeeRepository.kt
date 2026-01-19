package com.example.front.data.repository

import com.example.front.data.api.ApiService
import com.example.front.data.model.Employee
import com.example.front.data.model.EmployeeCreateRequest
import com.example.front.data.model.EmployeeUpdateRequest
import com.example.front.util.Resource

class EmployeeRepository(private val apiService: ApiService) {
    
    suspend fun getEmployees(search: String? = null, departmentId: Long? = null): Resource<List<Employee>> {
        return try {
            val response = apiService.getEmployees(search, departmentId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка загрузки сотрудников")
        }
    }
    
    suspend fun getEmployeeById(id: Long): Resource<Employee> {
        return try {
            val response = apiService.getEmployeeById(id)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка загрузки сотрудника")
        }
    }
    
    suspend fun createEmployee(request: EmployeeCreateRequest): Resource<Employee> {
        return try {
            val response = apiService.createEmployee(request)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка создания сотрудника")
        }
    }
    
    suspend fun updateEmployee(id: Long, request: EmployeeUpdateRequest): Resource<Employee> {
        return try {
            val response = apiService.updateEmployee(id, request)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка обновления сотрудника")
        }
    }
    
    suspend fun deleteEmployee(id: Long): Resource<Unit> {
        return try {
            apiService.deleteEmployee(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка удаления сотрудника")
        }
    }
}
