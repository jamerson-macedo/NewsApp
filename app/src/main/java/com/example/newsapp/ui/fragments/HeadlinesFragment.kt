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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.databinding.FragmentHeadlinesBinding
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewlModel
import com.example.newsapp.util.Constants
import com.example.newsapp.util.Resource


class HeadlinesFragment : Fragment(R.layout.fragment_headlines) {
    lateinit var newsViewlModel: NewsViewlModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var binding: FragmentHeadlinesBinding
    lateinit var retryButton: Button
    lateinit var errorText: TextView
    lateinit var itemHeadlinesError: CardView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeadlinesBinding.bind(view)

        itemHeadlinesError = view.findViewById(R.id.itemHeadlinesError)
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.item_error, null)

        retryButton = view.findViewById(R.id.retryButton)
        errorText = view.findViewById(R.id.errorText)

        newsViewlModel = (activity as NewsActivity).viewlModel
        configureRecyclerview()
        newsAdapter.setOnClickItemListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_headlinesFragment2_to_articleFragment,bundle)
        }
        newsViewlModel.headLines.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Sucess<*> -> {
                    hideErrorMessage()
                    hideProgressBar()
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles.toList())
                        // vai sxer 3 pages
                        val totalpages = it.totalResults / Constants.QUERY_PAGE + 2
                        isLastPage = newsViewlModel.headLinePages == totalpages
                        if (isLastPage) {
                            binding.recyclerHeadlines.setPadding(0, 0, 0, 0)
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
            newsViewlModel.getHeadlines("us")
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
        itemHeadlinesError.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessage(message: String) {
        itemHeadlinesError.visibility = View.VISIBLE
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
                newsViewlModel.getHeadlines("us")
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

    private fun configureRecyclerview() {
        newsAdapter = NewsAdapter()
        binding.recyclerHeadlines.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@HeadlinesFragment.scrollListener)
        }
    }
}