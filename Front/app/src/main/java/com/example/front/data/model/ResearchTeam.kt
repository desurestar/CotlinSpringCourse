package com.example.front.data.model

import com.google.gson.annotations.SerializedName

data class ResearchTeam(
    val id: Long,
    val name: String,
    val description: String?,
    val leader: Employee?,
    val createdAt: String?,
    val members: List<TeamMember>? = null,
    @SerializedName("works")
    val researchWorks: List<TeamResearchWork>? = null
)

data class ResearchTeamCreateRequest(
    val name: String,
    val description: String?,
    val leaderId: Long,
    val memberIds: List<Long>? = null
)
