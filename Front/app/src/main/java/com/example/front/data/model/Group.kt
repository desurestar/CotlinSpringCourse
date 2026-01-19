package com.example.front.data.model

data class Group(
    val id: Long,
    val groupName: String,
    val studentCount: Int? = null
)

data class GroupDetail(
    val id: Long,
    val groupName: String,
    val students: List<Student>? = null
)

data class GroupRequest(
    val groupName: String
)
