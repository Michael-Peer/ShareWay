package com.example.shareway.listeners

import com.example.shareway.viewholders.ArticleListViewHolder

interface RowMovesListener {
    fun onRowMoved(fromPosition: Int, toPosition: Int)
    fun onRowSelected(itemViewHolder: ArticleListViewHolder)
    fun onRowClear(itemViewHolder: ArticleListViewHolder)
}