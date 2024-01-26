package com.example.newsapp.repository

import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.db.ArticleDataBase
import com.example.newsapp.models.Article

// AQUI FICA TODOS OS BD
class ArticleRepository(val db: ArticleDataBase) {
    suspend fun getHeadLines(countryCode: String, pagenumber: Int) =
        RetrofitInstance.api.getHeadlines(countryCode, pagenumber)

    suspend fun searchQuery(query: String, pageNumber: Int) =
        RetrofitInstance.api.searchNews(query, pageNumber)

    suspend fun upSert(article: Article) =
        db.getArticleDAO().upSert(article)
    fun getAllArticles()=db.getArticleDAO().getAllArticles()
    suspend fun deleteArticle(article: Article)=db.getArticleDAO().deleteArticle(article)
}


