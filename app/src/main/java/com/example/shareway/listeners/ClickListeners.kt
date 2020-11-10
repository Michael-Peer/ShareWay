package com.example.shareway.listeners

import com.example.shareway.adapters.ArticleListAdapter
import com.example.shareway.models.Article
import java.time.Instant

interface OnCategoryClickListener {
    fun onCategoryClick(position: Int)
    fun onTextClick(position: Int)
    fun onCheckIconClick(newCategoryName: String, position: Int)
    fun onResetIconClick(adapterPosition: Int)
    fun onEnterEditMode(adapterPosition: Int)
    fun onExitEditMode(adapterPosition: Int)
    fun onMarkAsRead(adapterPosition: Int)
    fun onDelete(adapterPosition: Int)
    }



interface OnArticleClickListener {
    fun onArticleClick(position: Int)
    fun onLongArticleClick(position: Int)
    fun onEnterMultiSelectionMode(articleListAdapter: ArticleListAdapter)
    fun onDeleteMultipleArticles(articles: List<Article>)
    fun onMarkAsReadMultipleArticles(articles: List<Article>)
    fun onReminderSet(
        position: Int,
        reminder: Instant,
        hour: Int,
        minute: Int,
        day: Int
    )
    fun onReminderIconClick(text: String, position: Int)
    fun onAddNoteClick(adapterPosition: Int)
    fun onViewNotesClick(adapterPosition: Int)
    fun onDeleteArticle(adapterPosition: Int)
}


interface OnNoteClickListener {
    fun onNoteClick(position: Int)
}
