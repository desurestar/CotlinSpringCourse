package com.example.front.data.repository

import com.example.front.data.api.ApiService
import com.example.front.data.model.*
import com.example.front.util.Resource

class ResearchTeamRepository(private val apiService: ApiService) {
    
    suspend fun getResearchTeams(): Resource<List<ResearchTeam>> {
        return try {
            val response = apiService.getResearchTeams()
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка загрузки коллективов")
        }
    }
    
    suspend fun getResearchTeamById(id: Long): Resource<ResearchTeam> {
        return try {
            val response = apiService.getResearchTeamById(id)

            // If members are missing in the main response, try to fetch them via dedicated endpoint
            val members = response.members
            return if (members == null || members.isEmpty()) {
                try {
                    val membersResp = apiService.getTeamMembers(id)
                    // Create a new ResearchTeam instance with members filled
                    val filled = ResearchTeam(
                        id = response.id,
                        name = response.name,
                        description = response.description,
                        leader = response.leader,
                        createdAt = response.createdAt,
                        members = membersResp,
                        researchWorks = response.researchWorks
                    )
                    Resource.Success(filled)
                } catch (e: Exception) {
                    // If members endpoint fails, still return original response
                    Resource.Success(response)
                }
            } else {
                Resource.Success(response)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка загрузки коллектива")
        }
    }
    
    suspend fun getTeamMembers(teamId: Long): Resource<List<TeamMember>> {
        return try {
            val response = apiService.getTeamMembers(teamId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка загрузки участников")
        }
    }
    
    suspend fun createTeam(request: ResearchTeamCreateRequest): Resource<ResearchTeam> {
        return try {
            val response = apiService.createResearchTeam(request)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка создания коллектива")
        }
    }
    
    suspend fun deleteTeam(id: Long): Resource<Unit> {
        return try {
            val response = apiService.deleteResearchTeam(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Не удалось удалить коллектив")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка удаления")
        }
    }
    
    suspend fun addEmployeeToTeam(request: TeamMemberRequest): Resource<TeamMember> {
        return try {
            val response = apiService.addEmployeeToTeam(request)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка добавления участника")
        }
    }
    
    suspend fun addStudentToTeam(request: TeamMemberRequest): Resource<TeamMember> {
        return try {
            val response = apiService.addStudentToTeam(request)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка добавления участника")
        }
    }
    
    suspend fun createWork(request: TeamResearchWorkRequest): Resource<TeamResearchWork> {
        return try {
            val response = apiService.createWork(request)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка создания работы")
        }
    }
    
    suspend fun getTeamsByEmployee(employeeId: Long): Resource<List<ResearchTeam>> {
        return try {
            val response = apiService.getTeamsByEmployee(employeeId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка загрузки коллективов сотрудника")
        }
    }
}
