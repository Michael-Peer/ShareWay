package com.example.shareway.viewstates

import com.example.shareway.models.Article
import com.example.shareway.utils.UIComponentType

sealed class ArticlesViewState {
    object Loading : ArticlesViewState()

    data class Error(
        val errorMessage: String,
        val messageType: UIComponentType
    ) : ArticlesViewState()

    data class ArticleList(
        val articles: List<Article>
    ) : ArticlesViewState()
}