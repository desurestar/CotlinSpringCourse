package com.example.front.data.repository

import com.example.front.data.api.ApiService
import com.example.front.data.model.Article
import com.example.front.data.model.ArticleCreateRequest
import com.example.front.util.Resource

class ArticleRepository(private val apiService: ApiService) {
    
    suspend fun getAllArticles(): Resource<List<Article>> {
        return try {
            val response = apiService.getAllArticles()
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка загрузки статей")
        }
    }
    
    suspend fun getArticlesByEmployee(employeeId: Long): Resource<List<Article>> {
        return try {
            val response = apiService.getArticlesByEmployee(employeeId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка загрузки статей")
        }
    }
    
    suspend fun getArticleById(id: Long): Resource<Article> {
        return try {
            val response = apiService.getArticleById(id)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка загрузки статьи")
        }
    }
    
    suspend fun searchArticles(query: String): Resource<List<Article>> {
        return try {
            val response = apiService.searchArticles(query)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка поиска статей")
        }
    }
    
    suspend fun createArticle(request: ArticleCreateRequest): Resource<Article> {
        return try {
            android.util.Log.d("ArticleRepository", "Creating article with request: $request")
            val response = apiService.createArticle(request)
            android.util.Log.d("ArticleRepository", "Article created successfully: ${response.id}")
            Resource.Success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            android.util.Log.e("ArticleRepository", "HTTP Error creating article: ${e.code()} - $errorBody", e)
            Resource.Error("Ошибка создания статьи: ${e.code()} - ${errorBody ?: e.message()}")
        } catch (e: Exception) {
            android.util.Log.e("ArticleRepository", "Error creating article", e)
            Resource.Error("Ошибка создания статьи: ${e.message}")
        }
    }
    
    suspend fun updateArticle(id: Long, request: ArticleCreateRequest): Resource<Article> {
        return try {
            val response = apiService.updateArticle(id, request)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка обновления статьи")
        }
    }
    
    suspend fun deleteArticle(id: Long): Resource<Unit> {
        return try {
            val response = apiService.deleteArticle(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                val code = response.code()
                val body = try { response.errorBody()?.string() } catch (e: Exception) { null }
                android.util.Log.e("ArticleRepository", "Delete failed: code=$code, body=$body")
                Resource.Error("Не удалось удалить статью: ${code}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка удаления статьи")
        }
    }
}
