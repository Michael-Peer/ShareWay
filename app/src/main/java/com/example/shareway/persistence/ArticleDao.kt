package com.example.shareway.persistence

import androidx.room.*
import com.example.shareway.models.Article
import kotlinx.coroutines.flow.Flow
import java.time.Instant


@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles")
    fun getAllArticles(): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE url = :url")
    fun getArticle(url: String): Flow<Article>

    @Query("SELECT * FROM articles WHERE domainName = :categoryName")
    fun getAllArticlesByCategoryName(categoryName: String): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE domainName = :categoryName AND alreadyRead = :alreadyRead")
    fun getFilteredArticles(categoryName: String, alreadyRead: Boolean): Flow<List<Article>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticle(article: Article)

//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insertArticleWorker(article: Article): Long

    @Delete
    suspend fun deleteArticle(article: Article)

    @Delete
    suspend fun deleteArticles(article: List<Article>)

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles()

    @Query("UPDATE articles SET alreadyRead = NOT alreadyRead WHERE url = :url")
    suspend fun updateAlreadyRead(url: String)

    //1 = true, 0 = false
    @Query("UPDATE articles SET alreadyRead = 1 WHERE url in (:articleUrlList)")
    suspend fun updateMultipleMarkAsRead(articleUrlList: List<String>)

    @Query("UPDATE articles SET reminder = :reminder WHERE url = :currentArticleUrl")
    suspend fun updateReminder(currentArticleUrl: String, reminder: Instant)

    @Query("UPDATE articles SET reminder = null WHERE url = :articleUrl")
    suspend fun cancelReminder(articleUrl: String)

    @Query("SELECT * FROM articles WHERE domainName = :categoryName")
    suspend fun getRelatedArticles(categoryName: String): List<Article>

//    @Query("UPDATE articles SET alreadyRead = :alreadyRead   WHERE url = :url")
//    suspend fun updateAlreadyReadField(alreadyRead: Boolean, url: String)



}