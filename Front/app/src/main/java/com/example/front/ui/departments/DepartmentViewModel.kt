package com.example.front.ui.departments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.data.model.Department
import com.example.front.data.model.Employee
import com.example.front.data.repository.DepartmentRepository
import com.example.front.data.repository.EmployeeRepository
import com.example.front.util.Resource
import kotlinx.coroutines.launch

class DepartmentViewModel(
    private val departmentRepository: DepartmentRepository,
    private val employeeRepository: EmployeeRepository
) : ViewModel() {
    
    private val _departments = MutableLiveData<Resource<List<Department>>>()
    val departments: LiveData<Resource<List<Department>>> = _departments
    
    private val _department = MutableLiveData<Resource<Department>>()
    val department: LiveData<Resource<Department>> = _department
    
    private val _departmentEmployees = MutableLiveData<Resource<List<Employee>>>()
    val departmentEmployees: LiveData<Resource<List<Employee>>> = _departmentEmployees
    
    fun loadDepartments() {
        viewModelScope.launch {
            _departments.value = Resource.Loading()
            _departments.value = departmentRepository.getDepartments()
        }
    }
    
    fun searchDepartments(query: String?) {
        viewModelScope.launch {
            _departments.value = Resource.Loading()
            _departments.value = departmentRepository.getDepartments(query)
        }
    }
    
    fun loadDepartmentById(id: Long) {
        viewModelScope.launch {
            _department.value = Resource.Loading()
            // Load department info separately
            val departmentResult = departmentRepository.getDepartmentBasicInfo(id)
            _department.value = departmentResult
            
            // Load employees for this department
            if (departmentResult is Resource.Success) {
                loadDepartmentEmployees(id)
            }
        }
    }
    
    fun loadDepartmentEmployees(departmentId: Long) {
        viewModelScope.launch {
            _departmentEmployees.value = Resource.Loading()
            _departmentEmployees.value = employeeRepository.getEmployeesByDepartment(departmentId)
        }
    }
}
