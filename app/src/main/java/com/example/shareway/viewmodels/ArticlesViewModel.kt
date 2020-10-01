package com.example.shareway.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shareway.models.Article
import com.example.shareway.repositories.ArticleRepository
import com.example.shareway.utils.modes.FilterMode
import com.example.shareway.viewstates.ArticlesViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect

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


//    suspend fun getArticles(categoryName: String) {
//        articleRepository.getAllArticleByCategory(categoryName).collect {
//            _viewState.postValue(it)
//        }
//    }

    suspend fun getArticles(
        categoryName: String,
        filterMode: FilterMode = FilterMode.ALL

    ) {
        Log.d(TAG, "getArticles: $filterMode")
        articleRepository.getAllArticleByCategory(categoryName, filterMode).collect {
            _viewState.postValue(it)
        }
    }

    fun insertArticle(article: Article) {
        articleRepository.insertArticle(article)
    }

    fun updateAlreadyRead(url: String) {
        articleRepository.updateAlreadyRead(url)
    }

    fun deleteArticles(articles: List<Article>) {
        Log.d(TAG, "deleteArticles: $articles")
        articleRepository.deleteArticles(articles)
    }

    fun updateMultipleMarkAsRead(articles: List<Article>) {

        val articleUrlList: List<String> = convertToIdList(articles)

        Log.d(TAG, "updateMultipleMarkAsRead: $articleUrlList")
        articleRepository.updateMultipleMarkAsRead(articleUrlList)
    }

    private fun convertToIdList(articles: List<Article>): List<String> {
        val urlList = arrayListOf<String>()

        for (article in articles) {
            urlList.add(article.url)
        }
        return urlList
    }

    fun deleteArticle(article: Article) {
        articleRepository.deleteArticle(article)
    }


//    fun incrementCategoryAlreadyReadField(domainName: String) {
//        articleRepository.incrementCategoryAlreadyReadField(domainName)
//    }
//
//    fun decrementCategoryAlreadyReadField(domainName: String) {
//        articleRepository.decrementCategoryAlreadyReadField(domainName)
//    }

}