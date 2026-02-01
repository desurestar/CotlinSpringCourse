package com.example.front.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.data.model.Article
import com.example.front.data.model.Employee
import com.example.front.data.model.ResearchTeam
import com.example.front.data.repository.ArticleRepository
import com.example.front.data.repository.EmployeeRepository
import com.example.front.data.repository.ResearchTeamRepository
import com.example.front.util.Resource
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val employeeRepository: EmployeeRepository,
    private val articleRepository: ArticleRepository,
    private val researchTeamRepository: ResearchTeamRepository
) : ViewModel() {
    
    private val _employee = MutableLiveData<Resource<Employee>>()
    val employee: LiveData<Resource<Employee>> = _employee
    
    private val _myArticles = MutableLiveData<Resource<List<Article>>>()
    val myArticles: LiveData<Resource<List<Article>>> = _myArticles
    
    private val _participationArticles = MutableLiveData<Resource<List<Article>>>()
    val participationArticles: LiveData<Resource<List<Article>>> = _participationArticles
    
    private val _myTeams = MutableLiveData<Resource<List<ResearchTeam>>>()
    val myTeams: LiveData<Resource<List<ResearchTeam>>> = _myTeams
    
    private val _participationTeams = MutableLiveData<Resource<List<ResearchTeam>>>()
    val participationTeams: LiveData<Resource<List<ResearchTeam>>> = _participationTeams
    
    private val _deleteArticleResult = MutableLiveData<Resource<Unit>>()
    val deleteArticleResult: LiveData<Resource<Unit>> = _deleteArticleResult
    
    private val _deleteTeamResult = MutableLiveData<Resource<Unit>>()
    val deleteTeamResult: LiveData<Resource<Unit>> = _deleteTeamResult
    
    fun loadEmployeeProfile(employeeId: Long) {
        viewModelScope.launch {
            _employee.value = Resource.Loading()
            _employee.value = employeeRepository.getEmployeeById(employeeId)
        }
    }
    
    fun loadMyArticles(employeeId: Long) {
        viewModelScope.launch {
            _myArticles.value = Resource.Loading()
            val result = articleRepository.getArticlesByEmployee(employeeId)
            if (result is Resource.Success) {
                val articles = result.data ?: emptyList()
                // Filter only articles where employee is main author
                _myArticles.value = Resource.Success(articles.filter { it.mainAuthor?.id == employeeId })
            } else {
                _myArticles.value = result
            }
        }
    }
    
    fun loadParticipationArticles(employeeId: Long) {
        viewModelScope.launch {
            _participationArticles.value = Resource.Loading()
            val result = articleRepository.getArticlesByEmployee(employeeId)
            if (result is Resource.Success) {
                val articles = result.data ?: emptyList()
                // Filter only articles where employee is coauthor (not main author)
                _participationArticles.value = Resource.Success(
                    articles.filter { it.mainAuthor?.id != employeeId }
                )
            } else {
                _participationArticles.value = result
            }
        }
    }
    
    fun loadMyTeams(employeeId: Long) {
        viewModelScope.launch {
            _myTeams.value = Resource.Loading()
            val result = researchTeamRepository.getTeamsByEmployee(employeeId)
            if (result is Resource.Success) {
                val teams = result.data ?: emptyList()
                // Filter only teams where employee is leader
                _myTeams.value = Resource.Success(teams.filter { it.leader?.id == employeeId })
            } else {
                _myTeams.value = result
            }
        }
    }
    
    fun loadParticipationTeams(employeeId: Long) {
        viewModelScope.launch {
            _participationTeams.value = Resource.Loading()
            val result = researchTeamRepository.getTeamsByEmployee(employeeId)
            if (result is Resource.Success) {
                val teams = result.data ?: emptyList()
                // Filter only teams where employee is member (not leader)
                _participationTeams.value = Resource.Success(
                    teams.filter { it.leader?.id != employeeId }
                )
            } else {
                _participationTeams.value = result
            }
        }
    }
    
    fun deleteArticle(articleId: Long) {
        viewModelScope.launch {
            _deleteArticleResult.value = Resource.Loading()
            _deleteArticleResult.value = articleRepository.deleteArticle(articleId)
        }
    }
    
    fun deleteTeam(teamId: Long) {
        viewModelScope.launch {
            _deleteTeamResult.value = Resource.Loading()
            _deleteTeamResult.value = researchTeamRepository.deleteTeam(teamId)
        }
    }
    
    fun refreshAll(employeeId: Long) {
        loadEmployeeProfile(employeeId)
        loadMyArticles(employeeId)
        loadParticipationArticles(employeeId)
        loadMyTeams(employeeId)
        loadParticipationTeams(employeeId)
    }
}
