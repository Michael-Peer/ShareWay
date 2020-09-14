package com.example.shareway.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.shareway.models.Article
import com.example.shareway.repositories.ArticleRepository
import com.example.shareway.viewstates.ArticlesViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class ArticlesViewModel(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<ArticlesViewState>()
    val viewState: LiveData<ArticlesViewState>
        get() = _viewState


    suspend fun getArticles(categoryName: String) {
        articleRepository.getAllArticleByCategory(categoryName).collect {
            _viewState.postValue(it)
        }

    }

    fun insertArticle(article : Article) {
        articleRepository.insertArticle(article)
    }

}