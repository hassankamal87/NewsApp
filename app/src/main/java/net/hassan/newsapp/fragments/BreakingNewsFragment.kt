package net.hassan.newsapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import net.hassan.newsapp.R
import net.hassan.newsapp.adapters.NewsAdapter
import net.hassan.newsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import net.hassan.newsapp.util.Resource
import net.hassan.newsapp.viewmodel.NewsViewModel

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var newsAdapter: NewsAdapter
    private val viewModel: NewsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }

        newsAdapter.setOnSaveClickListener {
                viewModel.insertArticle(it)
                Snackbar.make(view, "Article Saved ", Snackbar.LENGTH_SHORT).show()
        }

        newsAdapter.setOnShareClickListener {
            val url = it.url
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, url)
            intent.type = "text/plain"
            val chooser = Intent.createChooser(intent, "Share Article from ${it.source} Using...")
            startActivity(chooser)
        }


        viewModel.newsMutableLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
                    it.data?.let { respon ->
                        newsAdapter.differ.submitList(respon.articles.toList())
                        val totalPages = respon.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if (isLastPage) {
                            recyclerBreakingNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    it.message?.let { message ->
                        Toast.makeText(activity, "error : $message", Toast.LENGTH_LONG).show()

                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                else -> {}
            }
        })
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisible = layoutManager.findFirstVisibleItemPosition()
            val visibleItems = layoutManager.childCount
            val totalItems = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisible + visibleItems >= totalItems
            val isNotAtBeginning = firstVisible >= 0
            val isTotalMoreThanVisible = totalItems >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.getBreakingNews("eg")
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }


    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter(viewModel)
        recyclerBreakingNews.adapter = newsAdapter
        recyclerBreakingNews.layoutManager = LinearLayoutManager(activity)
        recyclerBreakingNews.addOnScrollListener(this@BreakingNewsFragment.scrollListener)
    }
}