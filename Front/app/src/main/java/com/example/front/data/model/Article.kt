package com.example.front.data.model

data class Article(
    val id: Long,
    val title: String,
    val description: String,
    val externalLink: String?,
    val publicationDate: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val mainAuthor: Employee,
    val coauthors: List<Employee>?
)

data class ArticleCreateRequest(
    val title: String,
    val description: String,
    val externalLink: String?,
    val publicationDate: String?,
    val mainAuthorId: Long,
    val coauthorIds: List<Long>?
)
