package com.example.newsmvvm.ui

import androidx.lifecycle.ViewModel
import com.example.newsmvvm.repository.NewsRepository

class NewsViewModel(
    val newsRepository: NewsRepository
):ViewModel(){
}