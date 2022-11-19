package net.hassan.newsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*
import net.hassan.newsapp.R
import net.hassan.newsapp.ui.MainActivity
import net.hassan.newsapp.viewmodel.NewsViewModel

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private val viewModel: NewsViewModel by activityViewModels()
    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val article = args.article

        webView.apply {
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
        }

        fab.setOnClickListener {
            viewModel.insertArticle(article)
            Snackbar.make(view,"Article Saved ",Snackbar.LENGTH_SHORT).show()
        }

    }

}