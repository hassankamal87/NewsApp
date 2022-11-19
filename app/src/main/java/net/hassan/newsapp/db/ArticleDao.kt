package net.hassan.newsapp.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import net.hassan.newsapp.models.Article

@Dao
interface ArticleDao {

    //we need to try this fun to be suspend
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticle(article: Article):Long

    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)

}