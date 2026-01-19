package com.example.front.ui.employees

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.data.model.Employee
import com.example.front.data.repository.EmployeeRepository
import com.example.front.util.Resource
import kotlinx.coroutines.launch

class EmployeeViewModel(private val repository: EmployeeRepository) : ViewModel() {
    
    private val _employee = MutableLiveData<Resource<Employee>>()
    val employee: LiveData<Resource<Employee>> = _employee
    
    private val _employees = MutableLiveData<Resource<List<Employee>>>()
    val employees: LiveData<Resource<List<Employee>>> = _employees
    
    fun loadEmployeeById(id: Long) {
        viewModelScope.launch {
            _employee.value = Resource.Loading()
            _employee.value = repository.getEmployeeById(id)
        }
    }
    
    fun loadEmployees(search: String? = null, departmentId: Long? = null) {
        viewModelScope.launch {
            _employees.value = Resource.Loading()
            _employees.value = repository.getEmployees(search, departmentId)
        }
    }
}
