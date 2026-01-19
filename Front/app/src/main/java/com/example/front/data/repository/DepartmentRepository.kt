package com.example.front.data.repository

import com.example.front.data.api.ApiService
import com.example.front.data.model.Department
import com.example.front.data.model.DepartmentDetail
import com.example.front.data.model.DepartmentRequest
import com.example.front.util.Resource

class DepartmentRepository(private val apiService: ApiService) {
    
    suspend fun getDepartments(search: String? = null): Resource<List<Department>> {
        return try {
            val response = apiService.getDepartments(search)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка загрузки отделов")
        }
    }
    
    suspend fun getDepartmentById(id: Long): Resource<DepartmentDetail> {
        return try {
            val response = apiService.getDepartmentById(id)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка загрузки отдела")
        }
    }
    
    suspend fun createDepartment(name: String): Resource<Department> {
        return try {
            val response = apiService.createDepartment(DepartmentRequest(name))
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка создания отдела")
        }
    }
    
    suspend fun updateDepartment(id: Long, name: String): Resource<Department> {
        return try {
            val response = apiService.updateDepartment(id, DepartmentRequest(name))
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка обновления отдела")
        }
    }
    
    suspend fun deleteDepartment(id: Long): Resource<Unit> {
        return try {
            apiService.deleteDepartment(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка удаления отдела")
        }
    }
}
