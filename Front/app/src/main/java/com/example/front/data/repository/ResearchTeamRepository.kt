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
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка загрузки коллектива")
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
}
