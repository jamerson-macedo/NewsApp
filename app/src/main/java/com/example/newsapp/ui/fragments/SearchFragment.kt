package com.example.newsapp.ui.fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.databinding.FragmentSearchBinding
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewlModel
import com.example.newsapp.util.Constants
import com.example.newsapp.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.example.newsapp.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {
    lateinit var newsViewlModel: NewsViewlModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var binding: FragmentSearchBinding
    lateinit var retryButton: Button
    lateinit var errorText: TextView
    lateinit var itemSearchError: CardView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)


        itemSearchError = view.findViewById(R.id.itemSearchError)
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.item_error, null)

        retryButton = view.findViewById(R.id.retryButton)
        errorText = view.findViewById(R.id.errorText)
        newsViewlModel = (activity as NewsActivity).viewlModel
        setupRecycler()
        newsAdapter.setOnClickItemListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_searchFragment2_to_articleFragment, bundle)
        }
        var job: Job? = null
        // adicionando a função para veriricar mudanças no texto
        binding.searchEdit.addTextChangedListener() { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        newsViewlModel.searchNews(it.toString())
                    }

                }

            }

        }
        newsViewlModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Sucess<*> -> {
                    hideErrorMessage()
                    hideProgressBar()
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles.toList())
                        // vai sxer 3 pages
                        val totalpages = it.totalResults / Constants.QUERY_PAGE + 2
                        isLastPage = newsViewlModel.searchPage == totalpages
                        if (isLastPage) {
                            binding.recyclerSearch.setPadding(0, 0, 0, 0)
                        }

                    }
                }

                is Resource.Error<*> -> {
                    hideProgressBar()
                    response.data?.let { message ->
                        Toast.makeText(activity, "Sorry Error : $message", Toast.LENGTH_LONG).show()
                        showErrorMessage(message.toString())
                    }

                }

                is Resource.Loading<*> -> {
                    showProgressBar()
                }

            }
        })
        retryButton.setOnClickListener {
            if(binding.searchEdit.text.toString().isNotEmpty()){
                newsViewlModel.searchNews(binding.searchEdit.text.toString())
            }else{
                hideErrorMessage()
            }
        }

    }

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMessage() {
        itemSearchError.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessage(message: String) {
        itemSearchError.visibility = View.VISIBLE
        errorText.text = message
        isError = true
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            // so é chamado quando o scrool acaba
            val layoutmanager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItem = layoutmanager.findFirstVisibleItemPosition()
            val visibleitemcount = layoutmanager.childCount
            val totalItemCount = layoutmanager.itemCount

            val isnoError = !isError
            val isNotLoadingAndNoteLastPage = !isLoading && !isLastPage
            val islastPosition = firstVisibleItem + visibleitemcount >= totalItemCount
            val isTotalMorethanVisible = totalItemCount >= Constants.QUERY_PAGE
            val sholdPaginate =
                isnoError && isNotLoadingAndNoteLastPage && islastPosition && isTotalMorethanVisible
            if (sholdPaginate) {
                newsViewlModel.searchNews(binding.searchEdit.toString())
                isScrolling = false
            }


        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            // verirfica se o usuario westa fazendo scroll
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) isScrolling =
                true
        }

    }

    private fun setupRecycler() {
        newsAdapter = NewsAdapter()
        binding.recyclerSearch.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchFragment.scrollListener)
        }
    }

}