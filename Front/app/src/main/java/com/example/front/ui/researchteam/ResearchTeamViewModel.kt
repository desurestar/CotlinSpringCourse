package com.example.front.ui.researchteam

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.data.model.*
import com.example.front.data.repository.ResearchTeamRepository
import com.example.front.util.Resource
import kotlinx.coroutines.launch

class ResearchTeamViewModel(
    private val repository: ResearchTeamRepository
) : ViewModel() {
    
    private val _teams = MutableLiveData<Resource<List<ResearchTeam>>>()
    val teams: LiveData<Resource<List<ResearchTeam>>> = _teams
    
    private val _teamDetail = MutableLiveData<Resource<ResearchTeam>>()
    val teamDetail: LiveData<Resource<ResearchTeam>> = _teamDetail
    
    private val _createResult = MutableLiveData<Resource<ResearchTeam>>()
    val createResult: LiveData<Resource<ResearchTeam>> = _createResult
    
    fun loadTeams() {
        viewModelScope.launch {
            _teams.value = Resource.Loading()
            _teams.value = repository.getResearchTeams()
        }
    }
    
    fun loadTeamDetail(id: Long) {
        viewModelScope.launch {
            _teamDetail.value = Resource.Loading()
            _teamDetail.value = repository.getResearchTeamById(id)
        }
    }
    
    fun createTeam(name: String, description: String?, leaderId: Long) {
        viewModelScope.launch {
            _createResult.value = Resource.Loading()
            val request = ResearchTeamCreateRequest(name, description, leaderId)
            _createResult.value = repository.createTeam(request)
        }
    }
    
    fun addEmployee(teamId: Long, employeeId: Long, role: String) {
        viewModelScope.launch {
            val request = TeamMemberRequest(teamId, employeeId = employeeId, role = role)
            repository.addEmployeeToTeam(request)
            loadTeamDetail(teamId) // Reload
        }
    }
    
    fun addStudent(teamId: Long, studentId: Long, role: String) {
        viewModelScope.launch {
            val request = TeamMemberRequest(teamId, studentId = studentId, role = role)
            repository.addStudentToTeam(request)
            loadTeamDetail(teamId) // Reload
        }
    }
    
    fun createWork(teamId: Long, title: String, description: String, status: String) {
        viewModelScope.launch {
            val request = TeamResearchWorkRequest(teamId, title, description, status)
            repository.createWork(request)
            loadTeamDetail(teamId) // Reload
        }
    }
}
