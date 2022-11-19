package net.hassan.newsapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.hassan.newsapp.repository.Repo

class NewsViewModelFactory (
    val app:Application,
    val repo:Repo
    ): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(app,repo) as T
    }

}