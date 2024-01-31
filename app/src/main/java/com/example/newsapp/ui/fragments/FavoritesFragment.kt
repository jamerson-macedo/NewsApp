package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.databinding.FragmentFavoritesBinding
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewlModel
import com.google.android.material.snackbar.Snackbar


class FavoritesFragment : Fragment(R.layout.fragment_favorites) {
    lateinit var newsViewlModel: NewsViewlModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var binding: FragmentFavoritesBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavoritesBinding.bind(view)

        newsViewlModel = (activity as NewsActivity).viewlModel
        setupRecycler()
        newsAdapter.setOnClickItemListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_favoritesFragment_to_articleFragment, bundle)
        }
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                // pegando a posição do artigo
                val article = newsAdapter.differ.currentList[position]
                newsViewlModel.deleteArticle(article)
                Snackbar.make(view, "removed from favorites", Snackbar.LENGTH_SHORT).apply {
                    setAction("undo") {
                        newsViewlModel.addToFavorite(article)
                    }
                    show()
                }
            }
        }
        ItemTouchHelper(itemTouchCallback).apply {
            // seleciono o recycler
            attachToRecyclerView(binding.recyclerFavourites)
        }
        newsViewlModel.getFavoritesNews().observe(viewLifecycleOwner, Observer {
            // inserindo a lista que veio do bd
            newsAdapter.differ.submitList(it)
        })

    }


    private fun setupRecycler() {
        newsAdapter = NewsAdapter()
        binding.recyclerFavourites.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}