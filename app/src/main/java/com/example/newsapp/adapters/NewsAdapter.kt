package com.example.newsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.databinding.ItemNewsBinding
import com.example.newsapp.models.Article
import kotlinx.coroutines.withContext


class NewsAdapter():RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    inner class NewsViewHolder(val viewItem:ItemNewsBinding):RecyclerView.ViewHolder(viewItem.root)
        lateinit var articleImage:ImageView
        lateinit var articleSource:TextView
        lateinit var articleTitle:TextView
        lateinit var articleDescription:TextView
        lateinit var articleDateTime:TextView

        private val diffCallback=object :DiffUtil.ItemCallback<Article>(){
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.url==newItem.url
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem==newItem
            }
        }

        val differ=AsyncListDiffer(this,diffCallback)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            ItemNewsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
      return  differ.currentList.size
    }
    private var onItemClick:((Article)->Unit)?=null

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentNews=differ.currentList[position]
        holder.viewItem.apply {
            articleSource.text=currentNews.source.name
            articleDateTime.text=currentNews.publishedAt
            articleTitle.text=currentNews.title
            articleDescription.text=currentNews.description
            Glide.with(holder.itemView).load(currentNews.urlToImage).into(holder.viewItem.articleImage)
            holder.itemView.setOnClickListener {
                onItemClick?.let {
                    it(currentNews)
                }
            }
        }
    }
    fun setOnClickItemListener(listener:(Article)->Unit){
        onItemClick=listener
    }

}