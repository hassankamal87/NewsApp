package net.hassan.newsapp.models

class Respon (
    val status: String,
    val totalResults: Int,
    val articles: MutableList<Article>
)