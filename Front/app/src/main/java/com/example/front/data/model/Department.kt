package com.example.front.data.model

data class Department(
    val id: Long,
    val departmentName: String,
    val employeeCount: Int? = null
)

data class DepartmentDetail(
    val id: Long,
    val departmentName: String,
    val employees: List<Employee>? = null
)

data class DepartmentRequest(
    val departmentName: String
)
