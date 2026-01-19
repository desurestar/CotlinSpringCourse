package com.example.front.data.model

data class Student(
    val id: Long,
    val name: String,
    val group: Group?,
    val user: User?
)

data class StudentCreateRequest(
    val name: String,
    val groupId: Long,
    val email: String? = null,
    val password: String? = null,
    val role: String? = "STUDENT"
)

data class StudentUpdateRequest(
    val name: String,
    val groupId: Long
)
