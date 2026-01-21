package com.example.front.ui.researchteam

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.front.data.api.RetrofitClient
import com.example.front.data.local.PreferencesManager
import com.example.front.data.repository.ResearchTeamRepository

class ResearchTeamViewModelFactory(
    context: Context
) : ViewModelProvider.Factory {
    
    private val preferencesManager = PreferencesManager(context)
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResearchTeamViewModel::class.java)) {
            val apiService = RetrofitClient.getApiService { preferencesManager.getToken() }
            val repository = ResearchTeamRepository(apiService)
            @Suppress("UNCHECKED_CAST")
            return ResearchTeamViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
