package com.example.front.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.data.model.Article
import com.example.front.data.model.ArticleCreateRequest
import com.example.front.data.model.Employee
import com.example.front.data.model.ResearchTeam
import com.example.front.data.model.ResearchTeamCreateRequest
import com.example.front.data.repository.ArticleRepository
import com.example.front.data.repository.EmployeeRepository
import com.example.front.data.repository.ResearchTeamRepository
import com.example.front.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val employeeRepository: EmployeeRepository,
    private val articleRepository: ArticleRepository,
    private val researchTeamRepository: ResearchTeamRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "ProfileViewModel"
    }
    
    // StateFlow for current employee
    private val _currentEmployee = MutableStateFlow<Resource<Employee>>(Resource.Loading())
    val currentEmployee: StateFlow<Resource<Employee>> = _currentEmployee.asStateFlow()
    
    // StateFlow for articles
    private val _myArticles = MutableStateFlow<Resource<List<Article>>>(Resource.Loading())
    val myArticles: StateFlow<Resource<List<Article>>> = _myArticles.asStateFlow()
    
    private val _coauthoredArticles = MutableStateFlow<Resource<List<Article>>>(Resource.Loading())
    val coauthoredArticles: StateFlow<Resource<List<Article>>> = _coauthoredArticles.asStateFlow()
    
    // StateFlow for teams
    private val _myTeams = MutableStateFlow<Resource<List<ResearchTeam>>>(Resource.Loading())
    val myTeams: StateFlow<Resource<List<ResearchTeam>>> = _myTeams.asStateFlow()
    
    // LiveData for one-time events
    private val _teamCreationResult = MutableLiveData<Resource<ResearchTeam>>()
    val teamCreationResult: LiveData<Resource<ResearchTeam>> = _teamCreationResult
    
    private val _articleCreationResult = MutableLiveData<Resource<Article>>()
    val articleCreationResult: LiveData<Resource<Article>> = _articleCreationResult
    
    private val _articleDeletionResult = MutableLiveData<Resource<Unit>>()
    val articleDeletionResult: LiveData<Resource<Unit>> = _articleDeletionResult
    
    fun getCurrentEmployee(employeeId: Long) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Loading employee data for ID: $employeeId")
                _currentEmployee.value = Resource.Loading()
                val result = employeeRepository.getEmployeeById(employeeId)
                _currentEmployee.value = result
                
                when (result) {
                    is Resource.Success -> Log.d(TAG, "Employee loaded successfully: ${result.data?.name}")
                    is Resource.Error -> Log.e(TAG, "Error loading employee: ${result.message}")
                    is Resource.Loading -> {}
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading employee", e)
                _currentEmployee.value = Resource.Error("Ошибка загрузки профиля: ${e.message}")
            }
        }
    }
    
    fun refreshCurrentEmployee(employeeId: Long) {
        getCurrentEmployee(employeeId)
    }
    
    fun getEmployeeArticlesAsAuthor(employeeId: Long) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Loading articles where employee $employeeId is main author")
                _myArticles.value = Resource.Loading()
                val result = articleRepository.getArticlesByEmployee(employeeId)
                
                // Filter to only articles where employee is the main author
                if (result is Resource.Success && result.data != null) {
                    val mainAuthorArticles = result.data.filter { 
                        it.mainAuthor?.id == employeeId 
                    }
                    Log.d(TAG, "Found ${mainAuthorArticles.size} articles as main author")
                    _myArticles.value = Resource.Success(mainAuthorArticles)
                } else {
                    _myArticles.value = result
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading articles as author", e)
                _myArticles.value = Resource.Error("Ошибка загрузки статей: ${e.message}")
            }
        }
    }
    
    fun refreshEmployeeArticlesAsAuthor(employeeId: Long) {
        getEmployeeArticlesAsAuthor(employeeId)
    }
    
    fun getEmployeeArticlesAsCoauthor(employeeId: Long) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Loading articles where employee $employeeId is coauthor")
                _coauthoredArticles.value = Resource.Loading()
                val result = articleRepository.getArticlesByEmployee(employeeId)
                
                // Filter to only articles where employee is a coauthor (not main author)
                if (result is Resource.Success && result.data != null) {
                    val coauthoredArticles = result.data.filter { article ->
                        article.mainAuthor?.id != employeeId && 
                        article.coauthors?.any { coauthor -> coauthor.id == employeeId } == true
                    }
                    Log.d(TAG, "Found ${coauthoredArticles.size} articles as coauthor")
                    _coauthoredArticles.value = Resource.Success(coauthoredArticles)
                } else {
                    _coauthoredArticles.value = result
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading articles as coauthor", e)
                _coauthoredArticles.value = Resource.Error("Ошибка загрузки статей: ${e.message}")
            }
        }
    }
    
    fun refreshEmployeeArticlesAsCoauthor(employeeId: Long) {
        getEmployeeArticlesAsCoauthor(employeeId)
    }
    
    fun getEmployeeResearchTeams(employeeId: Long) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Loading teams for employee $employeeId using direct endpoint")
                _myTeams.value = Resource.Loading()
                
                // Use direct endpoint instead of filtering on client
                val teamsResult = researchTeamRepository.getTeamsByEmployee(employeeId)
                
                when (teamsResult) {
                    is Resource.Success -> {
                        val teams = teamsResult.data ?: emptyList()
                        Log.d(TAG, "Found ${teams.size} teams for employee")
                        _myTeams.value = Resource.Success(teams)
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "Error loading teams: ${teamsResult.message}")
                        _myTeams.value = Resource.Error(teamsResult.message ?: "Ошибка загрузки коллективов")
                    }
                    is Resource.Loading -> {
                        _myTeams.value = Resource.Loading()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading teams", e)
                _myTeams.value = Resource.Error("Ошибка загрузки коллективов: ${e.message}")
            }
        }
    }
    
    fun refreshEmployeeResearchTeams(employeeId: Long) {
        getEmployeeResearchTeams(employeeId)
    }
    
    fun createResearchTeam(request: ResearchTeamCreateRequest) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Creating research team: ${request.name}")
                _teamCreationResult.value = Resource.Loading()
                val result = researchTeamRepository.createTeam(request)
                _teamCreationResult.value = result
                
                when (result) {
                    is Resource.Success -> Log.d(TAG, "Team created successfully")
                    is Resource.Error -> Log.e(TAG, "Error creating team: ${result.message}")
                    is Resource.Loading -> {}
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception creating team", e)
                _teamCreationResult.value = Resource.Error("Ошибка создания коллектива: ${e.message}")
            }
        }
    }
    
    fun createArticle(request: ArticleCreateRequest) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Creating article: ${request.title}")
                Log.d(TAG, "Publication date: ${request.publicationDate}")
                _articleCreationResult.value = Resource.Loading()
                val result = articleRepository.createArticle(request)
                _articleCreationResult.value = result
                
                when (result) {
                    is Resource.Success -> Log.d(TAG, "Article created successfully")
                    is Resource.Error -> Log.e(TAG, "Error creating article: ${result.message}")
                    is Resource.Loading -> {}
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception creating article", e)
                _articleCreationResult.value = Resource.Error("Ошибка создания статьи: ${e.message}")
            }
        }
    }
    
    fun deleteArticle(articleId: Long) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Deleting article ID: $articleId")
                _articleDeletionResult.value = Resource.Loading()
                val result = articleRepository.deleteArticle(articleId)
                _articleDeletionResult.value = result
                
                when (result) {
                    is Resource.Success -> Log.d(TAG, "Article deleted successfully")
                    is Resource.Error -> Log.e(TAG, "Error deleting article: ${result.message}")
                    is Resource.Loading -> {}
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception deleting article", e)
                _articleDeletionResult.value = Resource.Error("Ошибка удаления статьи: ${e.message}")
            }
        }
    }
}
