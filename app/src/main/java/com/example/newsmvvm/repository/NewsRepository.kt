package com.example.newsmvvm.repository

import com.example.newsmvvm.RetrofitInstance
import com.example.newsmvvm.db.ArticleDatabase

class NewsRepository(
    val db:ArticleDatabase
) {
    suspend fun getBreakingNews(countryCode:String,pageNumber:Int)=
        RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)
}