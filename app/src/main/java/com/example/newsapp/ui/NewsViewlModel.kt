package com.example.newsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsapp.models.Article
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.repository.ArticleRepository
import com.example.newsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewlModel(app: Application, val repository: ArticleRepository) : AndroidViewModel(app) {
    val headLines: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var headLinePages = 1
    var headLineResponse: NewsResponse? = null


    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchPage = 1
    var searchNewsResponse: NewsResponse? = null
    var newSearchQuery: String? = null
    var oldSearchQuery: String? = null
    init {
        getHeadlines("us")
    }

    fun getHeadlines(country:String)=viewModelScope.launch {
        headlinesInternet(country)

    }
    fun searchNews(query:String)=viewModelScope.launch {
        searchInternet(query)
    }


    private fun handleHeadLineReponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { result ->
                headLinePages++
                // conta a pagina que ta mostrando
                Log.i("headlinePage",headLinePages.toString())
                if (headLineResponse == null) {
                    // se for a primeira vez ele pega o resultado e guarda na resposta
                    headLineResponse = result
                } else {
                    // pega ps que ja estao
                    val oldArticles = headLineResponse?.articles
                    // pega os novos
                    val newArticles = result.articles
                    // ju8nt acom os novos
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Sucess(headLineResponse ?: result)

            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { result ->
                if (searchNewsResponse == null || newSearchQuery != oldSearchQuery) {
                    searchPage = 1
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = result
                } else {
                    searchPage++
                    val oldArticles = searchNewsResponse?.articles
                    // pegando os novos
                    val newArticles = result.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Sucess(searchNewsResponse ?: result)

            }
        }
        return Resource.Error(response.message())
    }

    fun addToFavorite(article: Article) = viewModelScope.launch {
        repository.upSert(article)
    }

    fun getFavoritesNews() = repository.getAllArticles()
    fun deleteArticle(article: Article) = viewModelScope.launch {
        repository.deleteArticle(article)
    }

    fun internetConnection(context: Context): Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } ?: false
        }

    }

    private suspend fun headlinesInternet(countryCode: String) {
        headLines.postValue(Resource.Loading())
        try {
            if (internetConnection(this.getApplication())) {
                val response = repository.getHeadLines(countryCode, headLinePages)
                Log.i("responseView",response.toString())
                // aqui ele manda os artigos brutos
                headLines.postValue(handleHeadLineReponse(response))
            } else {
                headLines.postValue(Resource.Error("No internet"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> headLines.postValue(Resource.Error("Unable to connect"))
                else -> headLines.postValue(Resource.Error("no signal"))
            }
        }

    }

    private suspend fun searchInternet(query: String) {
        newSearchQuery = query
        searchNews.postValue(Resource.Loading())
        try {
            if (internetConnection(this.getApplication())) {
                val response = repository.searchQuery(query, searchPage)
                searchNews.postValue(handleSearchResponse(response))
            } else {
                searchNews.postValue(Resource.Error("No internet"))
            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Resource.Error("Unable to connect"))
                else -> searchNews.postValue(Resource.Error("no signal"))
            }
        }
    }
}