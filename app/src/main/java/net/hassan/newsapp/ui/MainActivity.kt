package net.hassan.newsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.hassan.newsapp.R
import net.hassan.newsapp.db.ArticlesDatabase
import net.hassan.newsapp.repository.Repo
import net.hassan.newsapp.viewmodel.NewsViewModel
import net.hassan.newsapp.viewmodel.NewsViewModelFactory

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val articlesDatabase = ArticlesDatabase(this)
        val repo = Repo(articlesDatabase)
        val newsViewModelFactory = NewsViewModelFactory(application,repo)
        viewModel = ViewModelProvider(this,newsViewModelFactory).get(NewsViewModel::class.java)
        setContentView(R.layout.activity_main)

        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())

    }
}