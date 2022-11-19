package net.hassan.newsapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.android.synthetic.main.fragment_search_news.paginationProgressBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.hassan.newsapp.R
import net.hassan.newsapp.adapters.NewsAdapter
import net.hassan.newsapp.models.Article
import net.hassan.newsapp.models.Respon
import net.hassan.newsapp.util.Constants
import net.hassan.newsapp.util.Constants.Companion.SEARCH_DELAY
import net.hassan.newsapp.util.Resource
import net.hassan.newsapp.viewmodel.NewsViewModel


class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

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
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

        newsAdapter.setOnSaveClickListener {
            viewModel.insertArticle(it)
            Snackbar.make(view,"Article Saved ", Snackbar.LENGTH_SHORT).show()
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


        var job: Job? = null
        etSearch.addTextChangedListener { myText ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_DELAY)
                myText?.let {
                    if(myText.toString().isNotEmpty()){
                        viewModel.searchForNews(it.toString())
                    }
                }
                if(myText.toString() == ""){
                    newsAdapter.differ.submitList(emptyList())
                }
            }
        }



        viewModel.searchMutableLiveData.observe(viewLifecycleOwner, Observer {

            when(it){
                is Resource.Success -> {
                    hideProgressBar()
                    it.data?.let { respon ->
                        newsAdapter.differ.submitList(respon.articles.toList())
                    }
                }

                is Resource.Error ->{
                    hideProgressBar()
                    it.message?.let { message ->
                        Toast.makeText(activity,"error : $message", Toast.LENGTH_LONG).show()
                    }
                }

                is Resource.Loading ->{
                    showProgressBar()
                }
                else -> {}
            }

        })
    }

    private fun hideProgressBar(){
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar(){
        paginationProgressBar.visibility = View.VISIBLE
    }


    private fun setUpRecyclerView(){
        newsAdapter = NewsAdapter()
        recyclerSearch.adapter = newsAdapter
        recyclerSearch.layoutManager = LinearLayoutManager(activity)
    }

}