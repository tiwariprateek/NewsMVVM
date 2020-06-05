package com.example.newsmvvm.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsmvvm.NewsApplication
import com.example.newsmvvm.models.Article
import com.example.newsmvvm.models.NewsResponse
import com.example.newsmvvm.repository.NewsRepository
import com.example.newsmvvm.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    val app: Application,
    val newsRepository: NewsRepository
): AndroidViewModel(app){
    val breakingNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1

    val searchNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage=1

    var breakingNewsResponse:NewsResponse?=null

    var searchNewsResponse:NewsResponse?=null


    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode:String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }
    fun searchNews(searchQuery:String)=viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    private fun handleBreakingNewsResponse(response:Response<NewsResponse>) : Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let {newsResponse ->
                breakingNewsPage++
                if(breakingNewsResponse==null){
                    breakingNewsResponse=newsResponse
                }
                else{
                    val oldArticles=breakingNewsResponse?.articles
                    val newArticles=newsResponse.articles
                    oldArticles?.addAll(newArticles)

                }
                return Resource.Success(breakingNewsResponse?:newsResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response:Response<NewsResponse>) : Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let {newsResponse ->
                searchNewsPage++
                if(searchNewsResponse==null){
                    searchNewsResponse=newsResponse
                }
                else{
                    val oldArticles=searchNewsResponse?.articles
                    val newArticles=newsResponse.articles
                    oldArticles?.addAll(newArticles)

                }
                return Resource.Success(searchNewsResponse?:newsResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    private suspend fun safeBreakingNewsCall(countryCode:String){
        breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            }
            else{
                breakingNews.postValue(Resource.Error("No internet connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else ->
                    breakingNews.postValue(Resource.Error("Conversion error"))
            }

        }

    }
    private suspend fun safeSearchNewsCall(searchQuery:String){
        searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }
            else{
                searchNews.postValue(Resource.Error("No internet connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else ->
                    searchNews.postValue(Resource.Error("Conversion error"))
            }

        }

    }

    private fun hasInternetConnection():Boolean{
        val connectivityManager = getApplication<NewsApplication>().
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> return true
            else -> false
            }
        }
        else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_ETHERNET -> true
                    TYPE_MOBILE -> true
                    else -> false
                }
            }
        }
    return false
    }
}