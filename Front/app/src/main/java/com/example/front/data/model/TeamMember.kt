package com.example.front.data.model

data class TeamMember(
    val id: Long,
    val employee: Employee?,
    val student: Student?,
    val role: String,
    val joinedAt: String?
) {
    fun getMemberName(): String {
        return employee?.name ?: student?.name ?: "Unknown"
    }
    
    fun getMemberType(): String {
        return if (employee != null) "Сотрудник" else "Студент"
    }
}

data class TeamMemberRequest(
    val teamId: Long,
    val employeeId: Long? = null,
    val studentId: Long? = null,
    val role: String? = "MEMBER"
)
