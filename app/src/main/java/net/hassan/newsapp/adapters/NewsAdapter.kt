package net.hassan.newsapp.adapters

import android.annotation.SuppressLint
import android.location.GnssAntennaInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.article_item.view.*
import net.hassan.newsapp.R
import net.hassan.newsapp.models.Article
import net.hassan.newsapp.viewmodel.NewsViewModel

class NewsAdapter(val newsViewModel: NewsViewModel? = null) : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {
    private var myList: ArrayList<Article> = ArrayList()

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.article_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val curArticle = differ.currentList[position]
        holder.itemView.apply {
            //setTexts
            tvSource.text = curArticle.source?.name
            tvTitle.text = curArticle.title
            tvDescription.text = curArticle.description
            tvPublishedAt.text = curArticle.publishedAt
            // set Image
            Glide.with(this).load(curArticle.urlToImage).into(ivArticleImage)

            //on click listener
            setOnClickListener{
                onItemClickListener?.let { it(curArticle) }
            }

            //on save clicked
            ivSave.setOnClickListener {
                onSaveClickListener?.let { it(curArticle) }
            }

            ivshare.setOnClickListener {
                onShareClickListener?.let { it(curArticle) }
            }

        }

    }

    // on all item clicked
    private var onItemClickListener: ((Article) -> Unit)? = null
    fun setOnItemClickListener(listener: (Article) -> Unit){
        onItemClickListener = listener
    }

    //on save button clicked
    private var onSaveClickListener: ((Article) -> Unit)? = null
    fun setOnSaveClickListener(listener: (Article) -> Unit){
        onSaveClickListener = listener
    }

    //on share button clicked
    private var onShareClickListener: ((Article) -> Unit)? = null
    fun setOnShareClickListener(listener: (Article) -> Unit){
        onShareClickListener = listener
    }



    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(myList: ArrayList<Article>) {
        this.myList = myList
        notifyDataSetChanged()
    }

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //TODO("here your views")

        init {

        }

    }


}