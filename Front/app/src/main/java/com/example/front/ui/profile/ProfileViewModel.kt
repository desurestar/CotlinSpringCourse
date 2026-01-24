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
    
    private val _currentEmployee = MutableLiveData<Resource<Employee>>()
    val currentEmployee: LiveData<Resource<Employee>> = _currentEmployee
    
    private val _myArticles = MutableLiveData<Resource<List<Article>>>()
    val myArticles: LiveData<Resource<List<Article>>> = _myArticles
    
    private val _coauthoredArticles = MutableLiveData<Resource<List<Article>>>()
    val coauthoredArticles: LiveData<Resource<List<Article>>> = _coauthoredArticles
    
    private val _myTeams = MutableLiveData<Resource<List<ResearchTeam>>>()
    val myTeams: LiveData<Resource<List<ResearchTeam>>> = _myTeams
    
    fun getCurrentEmployee(employeeId: Long) {
        viewModelScope.launch {
            _currentEmployee.value = Resource.Loading()
            _currentEmployee.value = employeeRepository.getEmployeeById(employeeId)
        }
    }
    
    fun getEmployeeArticlesAsAuthor(employeeId: Long) {
        viewModelScope.launch {
            _myArticles.value = Resource.Loading()
            val result = articleRepository.getArticlesByEmployee(employeeId)
            // Filter to only articles where employee is the main author
            if (result is Resource.Success && result.data != null) {
                val mainAuthorArticles = result.data.filter { it.mainAuthor.id == employeeId }
                _myArticles.value = Resource.Success(mainAuthorArticles)
            } else {
                _myArticles.value = result
            }
        }
    }
    
    fun getEmployeeArticlesAsCoauthor(employeeId: Long) {
        viewModelScope.launch {
            _coauthoredArticles.value = Resource.Loading()
            val result = articleRepository.getArticlesByEmployee(employeeId)
            // Filter to only articles where employee is a coauthor (not main author)
            if (result is Resource.Success && result.data != null) {
                val coauthoredArticles = result.data.filter { 
                    it.mainAuthor.id != employeeId && 
                    it.coauthors?.any { coauthor -> coauthor.id == employeeId } == true
                }
                _coauthoredArticles.value = Resource.Success(coauthoredArticles)
            } else {
                _coauthoredArticles.value = result
            }
        }
    }
    
    fun getEmployeeResearchTeams(employeeId: Long) {
        viewModelScope.launch {
            _myTeams.value = Resource.Loading()
            val teamsResult = researchTeamRepository.getResearchTeams()
            if (teamsResult is Resource.Success && teamsResult.data != null) {
                // Filter teams where employeeId is a leader or member
                val userTeams = teamsResult.data.filter { team ->
                    team.leader.id == employeeId || 
                    team.members?.any { it.employee?.id == employeeId } == true
                }
                _myTeams.value = Resource.Success(userTeams)
            } else {
                _myTeams.value = Resource.Error("Ошибка загрузки коллективов")
            }
        }
    }
}
