package net.hassan.newsapp.repository

import androidx.lifecycle.LiveData
import net.hassan.newsapp.api.RetrofitInstance
import net.hassan.newsapp.db.ArticlesDatabase
import net.hassan.newsapp.models.Article
import net.hassan.newsapp.models.Respon
import retrofit2.Response
import retrofit2.http.Query

class Repo(
    private val articlesDatabase: ArticlesDatabase
) {

    suspend fun getBreakingNews(countryCode:String,page:Int = 1):Response<Respon> =
        RetrofitInstance.newsApiInterface.getBreakingNews(countryCode)

    suspend fun searchForNews(searchQuery: String,page: Int = 1):Response<Respon> =
        RetrofitInstance.newsApiInterface.searchForNews(searchQuery)

    suspend fun insertArticle(article: Article) =
        articlesDatabase.getArticleDao().insertArticle(article)

    fun getAllArticle() =
        articlesDatabase.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) =
        articlesDatabase.getArticleDao().deleteArticle(article)

}