package com.example.newsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivityNewsBinding
import com.example.newsapp.db.ArticleDataBase
import com.example.newsapp.repository.ArticleRepository

class NewsActivity : AppCompatActivity() {
    lateinit var viewlModel: NewsViewlModel
    lateinit var binding: ActivityNewsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val newRepository=ArticleRepository(ArticleDataBase(this))
        val viewModelProviderFactory=NewsViewModelProviderFactory(application,newRepository)
        viewlModel= ViewModelProvider(this,viewModelProviderFactory).get(NewsViewlModel::class.java)

        val navHostFrament=supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController=navHostFrament.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}