package com.example.front.data.model

data class Employee(
    val id: Long,
    val name: String,
    val post: Post?,
    val department: Department?,
    val user: User?
)

data class EmployeeCreateRequest(
    val name: String,
    val postId: Long,
    val departmentId: Long,
    val email: String? = null,
    val password: String? = null,
    val role: String? = "EMPLOYEE"
)

data class EmployeeUpdateRequest(
    val name: String,
    val postId: Long,
    val departmentId: Long
)
