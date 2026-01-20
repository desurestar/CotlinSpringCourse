package com.example.front.data.model

data class TeamResearchWork(
    val id: Long,
    val title: String,
    val description: String,
    val status: String,
    val startDate: String?,
    val endDate: String?,
    val createdAt: String?
) {
    fun getStatusText(): String {
        return when (status) {
            "IN_PROGRESS" -> "В процессе"
            "COMPLETED" -> "Завершено"
            "PUBLISHED" -> "Опубликовано"
            "ON_HOLD" -> "Приостановлено"
            else -> status
        }
    }
    
    fun getStatusColor(): Int {
        return when (status) {
            "IN_PROGRESS" -> android.R.color.holo_blue_dark
            "COMPLETED" -> android.R.color.holo_green_dark
            "PUBLISHED" -> android.R.color.holo_purple
            "ON_HOLD" -> android.R.color.holo_orange_dark
            else -> android.R.color.darker_gray
        }
    }
}

data class TeamResearchWorkRequest(
    val teamId: Long,
    val title: String,
    val description: String,
    val status: String? = "IN_PROGRESS",
    val startDate: String? = null,
    val endDate: String? = null
)
