package com.example.shareway.repositories

import com.example.shareway.models.Article
import com.example.shareway.models.Note
import com.example.shareway.persistence.ArticleDao
import com.example.shareway.persistence.CategoryDao
import com.example.shareway.persistence.NotesDao
import com.example.shareway.utils.modes.FilterMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant


@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class ArticleRepository(
    private val appScope: CoroutineScope,
    private val articleDao: ArticleDao,
    private val categoryDao: CategoryDao, // TODO: Think of other way, don't think it's good practice to mix,
    private val notesDao: NotesDao // TODO: Think of other way, don't think it's good practice to mix
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
    ): Flow<List<Article>> {
        return filteredArticles(filterMode, categoryName).map {
            it
        }
    }

    private fun filteredArticles(
        filterMode: FilterMode,
        categoryName: String
    ): Flow<List<Article>> {
        return when (filterMode) {
            FilterMode.ALL -> articleDao.getAllArticlesByCategoryName(categoryName)
            FilterMode.NOT_READ -> articleDao.getFilteredArticles(categoryName, false)
            FilterMode.ALREADY_READ -> articleDao.getFilteredArticles(categoryName, true)
        }
    }

//
//    fun getAllArticleByCategory(
//        categoryName: String,
//        filterMode: FilterMode
//    ): Flow<ArticlesViewState> = flow {
//
//        Log.d(TAG, "articleDao hascode: ${articleDao.hashCode()}")
//
//        emit(ArticlesViewState.Loading)
//
//        delay(1000)
//
//        try {
////            articleDao.getAllArticlesByCategoryName(categoryName = categoryName).collect {
////                emit(
////                    ArticlesViewState.ArticleList(
////                        articles = it
////                    )
////                )
////            }
//
//            when (filterMode) {
//
//                FilterMode.ALL -> {
//                    Log.d(TAG, "getAllArticleByCategory: ALL")
//                    articleDao.getAllArticlesByCategoryName(categoryName = categoryName).collect {
//                        emit(
//                            ArticlesViewState.ArticleList(
//                                articles = it
//                            )
//                        )
//                    }
//                }
//                FilterMode.ALREADY_READ -> {
//                    Log.d(TAG, "getAllArticleByCategory: ALREADY_READ")
//
//                    articleDao.getFilteredArticles(categoryName = categoryName, alreadyRead = true)
//                        .collect {
//                            emit(
//                                ArticlesViewState.ArticleList(
//                                    articles = it
//                                )
//                            )
//                        }
//                }
//                FilterMode.NOT_READ -> {
//                    Log.d(TAG, "getAllArticleByCategory: NOT_READ")
//
//                    articleDao.getFilteredArticles(categoryName = categoryName, alreadyRead = false)
//                        .collect {
//                            emit(
//                                ArticlesViewState.ArticleList(
//                                    articles = it
//                                )
//                            )
//                        }
//                }
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

    suspend fun insertArticle(article: Article) {
        with(appScope.coroutineContext) {
            articleDao.insertArticle(article)
        }

    }

    suspend fun updateAlreadyRead(url: String) {


        with(appScope.coroutineContext) {
            articleDao.updateAlreadyRead(url)


        }
    }

    suspend fun deleteArticles(articles: List<Article>) {


        with(appScope.coroutineContext) {
            articleDao.deleteArticles(articles)

        }
    }

    suspend fun updateMultipleMarkAsRead(articles: List<String>) {


        with(appScope.coroutineContext) {
            articleDao.updateMultipleMarkAsRead(articles)

        }
    }

    suspend fun deleteArticle(article: Article) {


        with(appScope.coroutineContext) {
            articleDao.deleteArticle(article)

        }
    }

    suspend fun insertReminder(currentArticle: Article, reminder: Instant) {

        with(appScope.coroutineContext) {
            articleDao.updateReminder(currentArticle.url, reminder)

        }
    }

    suspend fun cancelReminder(articleUrl: String) {


        with(appScope.coroutineContext) {
            articleDao.cancelReminder(articleUrl)

        }
    }

    suspend fun saveFilter(
        filterMode: FilterMode,
        domainName: String
    ) {
        with(appScope.coroutineContext) {
            categoryDao.saveFilter(filterMode, domainName)

        }
    }


    suspend fun insertNote(
        note: Note
    ) {

        with(appScope.coroutineContext) {
            notesDao.insertNote(note)
        }
    }

    fun getNotes(articleURL: String) :Flow<List<Note>>  = notesDao.getNotes(articleURL)






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

//@ExperimentalCoroutinesApi
//@InternalCoroutinesApi
//class ArticleRepository(
//    private val articleDao: ArticleDao,
//    private val categoryDao: CategoryDao // TODO: Think of other way, don't think it's good practice to mix
//) {
//
//    companion object {
//        private const val TAG = "ArticleRepository"
//    }
//
////    fun getAllArticleByCategory(categoryName: String): Flow<ArticlesViewState> = flow {
////
////        Log.d(TAG, "articleDao hascode: ${articleDao.hashCode()}")
////
////        emit(ArticlesViewState.Loading)
////
////        delay(4000)
////
////        try {
////            articleDao.getAllArticlesByCategoryName(categoryName = categoryName).collect {
////                emit(
////                    ArticlesViewState.ArticleList(
////                        articles = it
////                    )
////                )
////            }
////        } catch (e: Exception) {
////            emit(
////                ArticlesViewState.Error(
////                    errorMessage = "Unknown Error",
////                    messageType = UIComponentType.Toast
////                )
////            )
////        }
////
////    }.flowOn(Dispatchers.IO)
//
//    fun getAllArticleByCategory(
//        categoryName: String,
//        filterMode: FilterMode
//    ): Flow<ArticlesViewState> = flow {
//
//        Log.d(TAG, "articleDao hascode: ${articleDao.hashCode()}")
//
//        emit(ArticlesViewState.Loading)
//
//        delay(1000)
//
//        try {
////            articleDao.getAllArticlesByCategoryName(categoryName = categoryName).collect {
////                emit(
////                    ArticlesViewState.ArticleList(
////                        articles = it
////                    )
////                )
////            }
//
//            when (filterMode) {
//
//                FilterMode.ALL -> {
//                    Log.d(TAG, "getAllArticleByCategory: ALL")
//                    articleDao.getAllArticlesByCategoryName(categoryName = categoryName).collect {
//                        emit(
//                            ArticlesViewState.ArticleList(
//                                articles = it
//                            )
//                        )
//                    }
//                }
//                FilterMode.ALREADY_READ -> {
//                    Log.d(TAG, "getAllArticleByCategory: ALREADY_READ")
//
//                    articleDao.getFilteredArticles(categoryName = categoryName, alreadyRead = true)
//                        .collect {
//                            emit(
//                                ArticlesViewState.ArticleList(
//                                    articles = it
//                                )
//                            )
//                        }
//                }
//                FilterMode.NOT_READ -> {
//                    Log.d(TAG, "getAllArticleByCategory: NOT_READ")
//
//                    articleDao.getFilteredArticles(categoryName = categoryName, alreadyRead = false)
//                        .collect {
//                            emit(
//                                ArticlesViewState.ArticleList(
//                                    articles = it
//                                )
//                            )
//                        }
//                }
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
//
//    fun insertArticle(article: Article) {
//        CoroutineScope(Dispatchers.IO).launch {
//            articleDao.insertArticle(article)
//        }
//    }
//
//    fun updateAlreadyRead(url: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            articleDao.updateAlreadyRead(url)
//        }
//    }
//
//    fun deleteArticles(articles: List<Article>) {
//        Log.d(TAG, "deleteArticles: before CoroutineScope $articles")
//        CoroutineScope(Dispatchers.IO).launch {
//            Log.d(TAG, "deleteArticles: after CoroutineScope$articles")
//            articleDao.deleteArticles(articles)
//        }
//    }
//
//    fun updateMultipleMarkAsRead(articles: List<String>) {
//        CoroutineScope(Dispatchers.IO).launch {
//            articleDao.updateMultipleMarkAsRead(articles)
//        }
//    }
//
//    fun deleteArticle(article: Article) {
//        CoroutineScope(Dispatchers.IO).launch {
//            articleDao.deleteArticle(article)
//        }
//    }
//
//    fun insertReminder(currentArticle: Article, reminder: Instant) {
//        CoroutineScope(Dispatchers.IO).launch {
//            articleDao.updateReminder(currentArticle.url, reminder)
//        }
//    }
//
//    fun cancelReminder(articleUrl: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            articleDao.cancelReminder(articleUrl)
//        }
//    }
//
//    fun saveFilter(
//        filterMode: FilterMode,
//        domainName: String
//    ) {
//        CoroutineScope(Dispatchers.IO).launch {
//            categoryDao.saveFilter(filterMode, domainName)
//        }
//    }
//
//
////    fun incrementCategoryAlreadyReadField(domainName: String) {
////        CoroutineScope(Dispatchers.IO).launch {
////            categoryDao.incrementAlreadyReadField(domainName)
////        }
////    }
////
////    fun decrementCategoryAlreadyReadField(domainName: String) {
////        CoroutineScope(Dispatchers.IO).launch {
////            categoryDao.decrementAlreadyReadField(domainName)
////        }
////    }
//
////    fun updateAlreadyReadField(alreadyRead: Boolean, articleUrl: String) {
////        CoroutineScope(Dispatchers.IO).launch {
////            articleDao.updateAlreadyReadField(alreadyRead, articleUrl)
////        }    }
//
//}

