package com.example.front.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.data.model.Article
import com.example.front.data.model.Employee
import com.example.front.data.model.TeamMember
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
    
    private val _myTeams = MutableLiveData<Resource<List<TeamMember>>>()
    val myTeams: LiveData<Resource<List<TeamMember>>> = _myTeams
    
    fun getCurrentEmployee(userId: Long) {
        viewModelScope.launch {
            _currentEmployee.value = Resource.Loading()
            _currentEmployee.value = employeeRepository.getEmployeeById(userId)
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
            // Get all research teams and filter for teams where employee is a member
            val teamsResult = researchTeamRepository.getResearchTeams()
            if (teamsResult is Resource.Success && teamsResult.data != null) {
                val allMembers = mutableListOf<TeamMember>()
                for (team in teamsResult.data) {
                    val membersResult = researchTeamRepository.getTeamMembers(team.id)
                    if (membersResult is Resource.Success && membersResult.data != null) {
                        val employeeMembers = membersResult.data.filter { 
                            it.employee?.id == employeeId 
                        }
                        allMembers.addAll(employeeMembers)
                    }
                }
                _myTeams.value = Resource.Success(allMembers)
            } else {
                _myTeams.value = Resource.Error("Ошибка загрузки коллективов")
            }
        }
    }
}
