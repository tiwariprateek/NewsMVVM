package com.example.newsmvvm.models

import com.example.newsmvvm.models.Article

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)