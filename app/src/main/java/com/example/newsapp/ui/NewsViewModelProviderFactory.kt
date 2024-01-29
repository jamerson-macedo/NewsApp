package com.example.newsapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsapp.repository.ArticleRepository

class NewsViewModelProviderFactory(val application: Application,val db:ArticleRepository):ViewModelProvider.Factory{
    // classe que instancia o viewmodel
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewlModel(application,db) as T
    }
}