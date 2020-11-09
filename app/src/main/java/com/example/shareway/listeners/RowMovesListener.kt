package com.example.shareway.listeners

import com.example.shareway.viewholders.ArticleListViewHolder
import com.example.shareway.viewholders.CategoryListViewHolder

interface RowMovesListener {
    fun onRowMoved(fromPosition: Int, toPosition: Int)
    fun onRowSelected(itemViewHolder: CategoryListViewHolder)
    fun onRowClear(itemViewHolder: CategoryListViewHolder)
}