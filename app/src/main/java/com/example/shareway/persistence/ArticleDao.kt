package com.example.shareway.persistence

import androidx.room.*
import com.example.shareway.models.Article
import kotlinx.coroutines.flow.Flow


@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles")
    fun getAllArticles(): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE url = :url")
    fun getArticle(url: String): Flow<Article>

    @Query("SELECT * FROM articles WHERE domainName = :categoryName")
    fun getAllArticlesByCategoryName(categoryName: String): Flow<List<Article>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: Article)

    @Delete
    suspend fun deleteArticle(article: Article)

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles()

}