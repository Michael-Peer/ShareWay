package com.example.shareway.repositories

import android.util.Log
import com.example.shareway.models.Article
import com.example.shareway.persistence.ArticleDao
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

    fun getAllArticleByCategory(categoryName: String): Flow<ArticlesViewState> = flow {

        Log.d(TAG, "articleDao hascode: ${articleDao.hashCode()}")

        emit(ArticlesViewState.Loading)

        delay(4000)

        try {
            articleDao.getAllArticlesByCategoryName(categoryName = categoryName).collect {
                emit(
                    ArticlesViewState.ArticleList(
                        articles = it
                    )
                )
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
}