package net.hassan.newsapp.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.hassan.newsapp.models.Article
import net.hassan.newsapp.models.Respon
import net.hassan.newsapp.repository.Repo
import net.hassan.newsapp.ui.NewsApplication
import net.hassan.newsapp.util.Resource
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app: Application,
    private val repo: Repo
) : AndroidViewModel(app) {

    var newsMutableLiveData: MutableLiveData<Resource<Respon>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: Respon? = null


    var searchMutableLiveData: MutableLiveData<Resource<Respon>> = MutableLiveData()


    init {
        getBreakingNews("eg")
    }


    // for homepage
    fun getBreakingNews(countryCode: String) {
        viewModelScope.launch {
            safeBreakingNewsCall(countryCode)
        }
    }

    //لو مفيش نت هنعمل ايه
    private suspend fun safeBreakingNewsCall(countryCode: String) {
        newsMutableLiveData.postValue(Resource.Loading())
        try {
            if (connectedInternetOrNot()) {
                val response = repo.getBreakingNews(countryCode, breakingNewsPage)
                newsMutableLiveData.postValue(handleBreakingNewsResponse(response))
            } else {
                newsMutableLiveData.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> newsMutableLiveData.postValue(Resource.Error("Network Failure"))
                else -> newsMutableLiveData.postValue(Resource.Error("other Error"))
            }
        }
    }

    // get Resource by google
    private fun handleBreakingNewsResponse(response: Response<Respon>): Resource<Respon> {
        if (response.isSuccessful) {
            response.body()?.let {
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = it
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticle = it.articles
                    oldArticles?.addAll(newArticle)
                }
                return Resource.Success(breakingNewsResponse ?: it)
            }
        }
        return Resource.Error(response.message())
    }

    //for search
    fun searchForNews(searchQuery: String, page: Int = 1) {
        viewModelScope.launch {
            safeSearchNewsCall(searchQuery)
        }
    }

    //لو مفيش نت هنعمل ايه
    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchMutableLiveData.postValue(Resource.Loading())
        try {
            if (connectedInternetOrNot()) {
                val response = repo.searchForNews(searchQuery)
                searchMutableLiveData.postValue(handleSearchNewsResponse(response))
            } else {
                searchMutableLiveData.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchMutableLiveData.postValue(Resource.Error("Network Failure"))
                else -> searchMutableLiveData.postValue(Resource.Error("other Error"))
            }
        }
    }

    //for get Resource by google
    private fun handleSearchNewsResponse(response: Response<Respon>): Resource<Respon> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }


    //room database
    fun insertArticle(article: Article) {


        viewModelScope.launch {
            repo.insertArticle(article)

        }
    }

    fun getAllArticlesSaved() =
        repo.getAllArticle()

    fun deleteArticle(article: Article) =
        viewModelScope.launch {
            repo.deleteArticle(article)
        }


    //check if there Internet or not
    private fun connectedInternetOrNot(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}