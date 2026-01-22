package com.example.front.ui.articles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.data.model.Article
import com.example.front.data.repository.ArticleRepository
import com.example.front.util.Resource
import kotlinx.coroutines.launch

class ArticleViewModel(
    private val articleRepository: ArticleRepository
) : ViewModel() {
    
    private val _articles = MutableLiveData<Resource<List<Article>>>()
    val articles: LiveData<Resource<List<Article>>> = _articles
    
    private val _selectedArticle = MutableLiveData<Resource<Article>>()
    val selectedArticle: LiveData<Resource<Article>> = _selectedArticle
    
    fun getAllArticles() {
        viewModelScope.launch {
            _articles.value = Resource.Loading()
            _articles.value = articleRepository.getAllArticles()
        }
    }
    
    fun searchArticles(query: String) {
        viewModelScope.launch {
            _articles.value = Resource.Loading()
            if (query.isEmpty()) {
                _articles.value = articleRepository.getAllArticles()
            } else {
                _articles.value = articleRepository.searchArticles(query)
            }
        }
    }
    
    fun getArticleById(id: Long) {
        viewModelScope.launch {
            _selectedArticle.value = Resource.Loading()
            _selectedArticle.value = articleRepository.getArticleById(id)
        }
    }
}
