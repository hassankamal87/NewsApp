package net.hassan.newsapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*
import kotlinx.android.synthetic.main.fragment_search_news.*
import net.hassan.newsapp.R
import net.hassan.newsapp.adapters.NewsAdapter
import net.hassan.newsapp.ui.MainActivity
import net.hassan.newsapp.viewmodel.NewsViewModel


class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {

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
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
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

        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(view,"ArticleDeleted ",Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.insertArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(recyclerSaved)
        }

        viewModel.getAllArticlesSaved().observe(viewLifecycleOwner, Observer {
            newsAdapter.differ.submitList(it)
        })
    }

    private fun setUpRecyclerView(){
        newsAdapter = NewsAdapter()
        recyclerSaved.adapter = newsAdapter
        recyclerSaved.layoutManager = LinearLayoutManager(activity)
    }
}