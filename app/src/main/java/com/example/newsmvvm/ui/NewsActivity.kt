package com.example.newsmvvm.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsmvvm.R
import com.example.newsmvvm.db.ArticleDatabase
import com.example.newsmvvm.repository.NewsRepository
import com.example.newsmvvm.repository.NewsViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {
    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        val newsRepository=NewsRepository(ArticleDatabase(this))
        val factory = NewsViewModelProviderFactory(application,newsRepository)
        viewModel=ViewModelProvider(this,factory).get(NewsViewModel::class.java)
        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())
    }
}
