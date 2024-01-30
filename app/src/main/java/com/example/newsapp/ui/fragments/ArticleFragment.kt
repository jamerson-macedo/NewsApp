package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentArticleBinding
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewlModel
import com.google.android.material.snackbar.Snackbar


class ArticleFragment : Fragment(R.layout.fragment_article) {
    lateinit var viewmodel: NewsViewlModel
    // para passar dados
    val args:ArticleFragmentArgs by navArgs()
   lateinit var binding: FragmentArticleBinding
   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding=FragmentArticleBinding.bind(view)
      // inicializando viewmodel
      viewmodel=(activity as NewsActivity).viewlModel

      val article=args.article
      Log.i("articleclick",article.toString())


      binding.webView.apply {
         webViewClient= WebViewClient()
         article.url?.let {
            loadUrl(it)
         }
      }
      binding.fab.setOnClickListener {
         viewmodel.addToFavorite(article)
         Snackbar.make(view,"Added with sucess",Snackbar.LENGTH_SHORT).show()
      }

   }

}