package com.example.front.ui.departments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.data.model.Department
import com.example.front.data.model.DepartmentDetail
import com.example.front.data.repository.DepartmentRepository
import com.example.front.util.Resource
import kotlinx.coroutines.launch

class DepartmentViewModel(private val repository: DepartmentRepository) : ViewModel() {
    
    private val _departments = MutableLiveData<Resource<List<Department>>>()
    val departments: LiveData<Resource<List<Department>>> = _departments
    
    private val _departmentDetail = MutableLiveData<Resource<DepartmentDetail>>()
    val departmentDetail: LiveData<Resource<DepartmentDetail>> = _departmentDetail
    
    fun loadDepartments() {
        viewModelScope.launch {
            _departments.value = Resource.Loading()
            _departments.value = repository.getDepartments()
        }
    }
    
    fun searchDepartments(query: String?) {
        viewModelScope.launch {
            _departments.value = Resource.Loading()
            _departments.value = repository.getDepartments(query)
        }
    }
    
    fun loadDepartmentById(id: Long) {
        viewModelScope.launch {
            _departmentDetail.value = Resource.Loading()
            _departmentDetail.value = repository.getDepartmentById(id)
        }
    }
}
