package com.example.shareway.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.shareway.models.Article
import com.example.shareway.repositories.ArticleRepository
import com.example.shareway.utils.modes.FilterMode
import com.example.shareway.viewstates.ArticlesViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.logging.Handler

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class ArticlesViewModel(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    companion object {
        private const val TAG = "ArticlesViewModel"

    }


    private val _viewState = MutableLiveData<ArticlesViewState>()
    val viewState: LiveData<ArticlesViewState>
        get() = _viewState

    private var lastSource: LiveData<ArticlesViewState.ArticleList>? = null
    private val _states = MediatorLiveData<ArticlesViewState>()
    val states: LiveData<ArticlesViewState> = _states


//    suspend fun getArticles(categoryName: String) {
//        articleRepository.getAllArticleByCategory(categoryName).collect {
//            _viewState.postValue(it)
//        }
//    }

//    suspend fun getArticles(
//        categoryName: String,
//        filterMode: FilterMode
//    ) {
//        Log.d(TAG, "getArticles: $filterMode")
//        articleRepository.getAllArticleByCategory(categoryName, filterMode).collect {
//            _viewState.postValue(it)
//        }
//
//        val items = articleRepository.getAllArticleByCategory(categoryName, filterMode).map {
//
//        }
//    }



    fun getAll(filterMode: FilterMode, categoryName: String) {

        _states.value = ArticlesViewState.Loading



        lastSource?.let {
            _states.removeSource(it)
        }

        val items = articleRepository.getAllArticleByCategory(categoryName, filterMode).map {
            ArticlesViewState.ArticleList(it)
        }.asLiveData()

        _states.addSource(items) {
            _states.value = it
        }

        lastSource = items
    }

    fun insertArticle(article: Article) {
        viewModelScope.launch {
        articleRepository.insertArticle(article)
        }
    }

    fun updateAlreadyRead(url: String) {
        viewModelScope.launch {
            articleRepository.updateAlreadyRead(url)

        }
    }

    fun deleteArticles(articles: List<Article>) {
        viewModelScope.launch {
            articleRepository.deleteArticles(articles)

        }
        Log.d(TAG, "deleteArticles: $articles")
    }

    fun updateMultipleMarkAsRead(articles: List<Article>) {
        viewModelScope.launch {
            val articleUrlList: List<String> = convertToIdList(articles)

            Log.d(TAG, "updateMultipleMarkAsRead: $articleUrlList")
            articleRepository.updateMultipleMarkAsRead(articleUrlList)
        }


    }

    private fun convertToIdList(articles: List<Article>): List<String> {
        val urlList = arrayListOf<String>()

        for (article in articles) {
            urlList.add(article.url)
        }
        return urlList
    }

    fun deleteArticle(article: Article) {
        viewModelScope.launch {
            articleRepository.deleteArticle(article)

        }
    }

    fun insertReminder(currentArticle: Article, reminder: Instant) {
        viewModelScope.launch {
            articleRepository.insertReminder(currentArticle,reminder)

        }
    }

    fun cancelReminder(articleUrl: String) {
        viewModelScope.launch {
            articleRepository.cancelReminder(articleUrl)

        }
    }

    fun saveFilter(
        filterMode: FilterMode,
        domainName: String
    ) {
        viewModelScope.launch {
            articleRepository.saveFilter(filterMode, domainName)

        }
    }

//    fun incrementCategoryAlreadyReadField(domainName: String) {
//        articleRepository.incrementCategoryAlreadyReadField(domainName)
//    }
//
//    fun decrementCategoryAlreadyReadField(domainName: String) {
//        articleRepository.decrementCategoryAlreadyReadField(domainName)
//    }

}

//@InternalCoroutinesApi
//@ExperimentalCoroutinesApi
//class ArticlesViewModel(
//    private val articleRepository: ArticleRepository
//) : ViewModel() {
//
//    companion object {
//        private const val TAG = "ArticlesViewModel"
//
//    }
//
//    private val _viewState = MutableLiveData<ArticlesViewState>()
//    val viewState: LiveData<ArticlesViewState>
//        get() = _viewState
//
//
////    suspend fun getArticles(categoryName: String) {
////        articleRepository.getAllArticleByCategory(categoryName).collect {
////            _viewState.postValue(it)
////        }
////    }
//
//    suspend fun getArticles(
//        categoryName: String,
//        filterMode: FilterMode
//    ) {
//        Log.d(TAG, "getArticles: $filterMode")
//        articleRepository.getAllArticleByCategory(categoryName, filterMode).collect {
//            _viewState.postValue(it)
//        }
//    }
//
//    fun insertArticle(article: Article) {
//        articleRepository.insertArticle(article)
//    }
//
//    fun updateAlreadyRead(url: String) {
//        articleRepository.updateAlreadyRead(url)
//    }
//
//    fun deleteArticles(articles: List<Article>) {
//        Log.d(TAG, "deleteArticles: $articles")
//        articleRepository.deleteArticles(articles)
//    }
//
//    fun updateMultipleMarkAsRead(articles: List<Article>) {
//
//        val articleUrlList: List<String> = convertToIdList(articles)
//
//        Log.d(TAG, "updateMultipleMarkAsRead: $articleUrlList")
//        articleRepository.updateMultipleMarkAsRead(articleUrlList)
//    }
//
//    private fun convertToIdList(articles: List<Article>): List<String> {
//        val urlList = arrayListOf<String>()
//
//        for (article in articles) {
//            urlList.add(article.url)
//        }
//        return urlList
//    }
//
//    fun deleteArticle(article: Article) {
//        articleRepository.deleteArticle(article)
//    }
//
//    fun insertReminder(currentArticle: Article, reminder: Instant) {
//        articleRepository.insertReminder(currentArticle,reminder)
//    }
//
//    fun cancelReminder(articleUrl: String) {
//        articleRepository.cancelReminder(articleUrl)
//    }
//
//    fun saveFilter(
//        filterMode: FilterMode,
//        domainName: String
//    ) {
//        articleRepository.saveFilter(filterMode, domainName)
//    }
//
////    fun incrementCategoryAlreadyReadField(domainName: String) {
////        articleRepository.incrementCategoryAlreadyReadField(domainName)
////    }
////
////    fun decrementCategoryAlreadyReadField(domainName: String) {
////        articleRepository.decrementCategoryAlreadyReadField(domainName)
////    }
//
//}