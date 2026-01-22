package com.example.front.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.front.data.repository.ArticleRepository
import com.example.front.data.repository.EmployeeRepository
import com.example.front.data.repository.ResearchTeamRepository

class ProfileViewModelFactory(
    private val employeeRepository: EmployeeRepository,
    private val articleRepository: ArticleRepository,
    private val researchTeamRepository: ResearchTeamRepository
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(employeeRepository, articleRepository, researchTeamRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
