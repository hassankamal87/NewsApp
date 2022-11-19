package net.hassan.newsapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "articles"
)
class Article(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val author: String?,
    val title: String?,
    val description: String?,
    val publishedAt: String?,
    val content: String?,
    val url: String?,
    val urlToImage: String?,
    val source: Source?
): Serializable
