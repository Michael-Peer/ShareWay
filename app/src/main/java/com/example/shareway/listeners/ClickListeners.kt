package com.example.shareway.listeners

import com.example.shareway.adapters.ArticleListAdapter
import com.example.shareway.models.Article

interface OnCategoryClickListener {
    fun onCategoryClick(position: Int)
    fun onTextClick(position: Int)
    fun onCheckIconClick(newCategoryName: String, position: Int)
    fun onResetIconClick(adapterPosition: Int)
    fun onEnterEditMode(adapterPosition: Int)
    fun onExitEditMode(adapterPosition: Int)
    }



interface OnArticleClickListener {
    fun onArticleClick(position: Int)
    fun onLongArticleClick(position: Int)
    fun onEnterMultiSelectionMode(articleListAdapter: ArticleListAdapter)
    fun onDeleteMultipleArticles(articles: List<Article>)
    fun onMarkAsReadMultipleArticles(articles: List<Article>)
    fun onSetRemainderButtonClick(position: Int)


}
