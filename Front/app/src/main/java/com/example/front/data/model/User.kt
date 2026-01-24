package com.example.front.data.model

data class User(
    val id: Long,
    val email: String,
    val role: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val userId: Long,
    val email: String,
    val role: String,
    val employeeId: Long?
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val role: String = "STUDENT"
)
