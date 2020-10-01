package com.example.shareway.repositories

import android.util.Log
import com.example.shareway.models.Article
import com.example.shareway.persistence.ArticleDao
import com.example.shareway.utils.modes.FilterMode
import com.example.shareway.utils.UIComponentType
import com.example.shareway.viewstates.ArticlesViewState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class ArticleRepository(
    private val articleDao: ArticleDao
) {

    companion object {
        private const val TAG = "ArticleRepository"
    }

//    fun getAllArticleByCategory(categoryName: String): Flow<ArticlesViewState> = flow {
//
//        Log.d(TAG, "articleDao hascode: ${articleDao.hashCode()}")
//
//        emit(ArticlesViewState.Loading)
//
//        delay(4000)
//
//        try {
//            articleDao.getAllArticlesByCategoryName(categoryName = categoryName).collect {
//                emit(
//                    ArticlesViewState.ArticleList(
//                        articles = it
//                    )
//                )
//            }
//        } catch (e: Exception) {
//            emit(
//                ArticlesViewState.Error(
//                    errorMessage = "Unknown Error",
//                    messageType = UIComponentType.Toast
//                )
//            )
//        }
//
//    }.flowOn(Dispatchers.IO)

    fun getAllArticleByCategory(
        categoryName: String,
        filterMode: FilterMode
    ): Flow<ArticlesViewState> = flow {

        Log.d(TAG, "articleDao hascode: ${articleDao.hashCode()}")

        emit(ArticlesViewState.Loading)

        delay(4000)

        try {
//            articleDao.getAllArticlesByCategoryName(categoryName = categoryName).collect {
//                emit(
//                    ArticlesViewState.ArticleList(
//                        articles = it
//                    )
//                )
//            }

            when (filterMode) {

                FilterMode.ALL -> {
                    Log.d(TAG, "getAllArticleByCategory: ALL")
                    articleDao.getAllArticlesByCategoryName(categoryName = categoryName).collect {
                        emit(
                            ArticlesViewState.ArticleList(
                                articles = it
                            )
                        )
                    }
                }
                FilterMode.ALREADY_READ -> {
                    Log.d(TAG, "getAllArticleByCategory: ALREADY_READ")

                    articleDao.getFilteredArticles(categoryName = categoryName, alreadyRead = true)
                        .collect {
                            emit(
                                ArticlesViewState.ArticleList(
                                    articles = it
                                )
                            )
                        }
                }
                FilterMode.NOT_READ -> {
                    Log.d(TAG, "getAllArticleByCategory: NOT_READ")

                    articleDao.getFilteredArticles(categoryName = categoryName, alreadyRead = false)
                        .collect {
                            emit(
                                ArticlesViewState.ArticleList(
                                    articles = it
                                )
                            )
                        }
                }
            }
        } catch (e: Exception) {
            emit(
                ArticlesViewState.Error(
                    errorMessage = "Unknown Error",
                    messageType = UIComponentType.Toast
                )
            )
        }

    }.flowOn(Dispatchers.IO)

    fun insertArticle(article: Article) {
        CoroutineScope(Dispatchers.IO).launch {
            articleDao.insertArticle(article)
        }
    }

    fun updateAlreadyRead(url: String) {
        CoroutineScope(Dispatchers.IO).launch {

            articleDao.updateAlreadyRead(url)
        }
    }

    fun deleteArticles(articles: List<Article>) {
        Log.d(TAG, "deleteArticles: before CoroutineScope $articles")
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "deleteArticles: after CoroutineScope$articles")
            articleDao.deleteArticles(articles)
        }
    }

    fun updateMultipleMarkAsRead(articles: List<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            articleDao.updateMultipleMarkAsRead(articles)
        }
    }

    fun deleteArticle(article: Article) {
        CoroutineScope(Dispatchers.IO).launch {
            articleDao.deleteArticle(article)
        }    }

//    fun incrementCategoryAlreadyReadField(domainName: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            categoryDao.incrementAlreadyReadField(domainName)
//        }
//    }
//
//    fun decrementCategoryAlreadyReadField(domainName: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            categoryDao.decrementAlreadyReadField(domainName)
//        }
//    }

//    fun updateAlreadyReadField(alreadyRead: Boolean, articleUrl: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            articleDao.updateAlreadyReadField(alreadyRead, articleUrl)
//        }    }

}