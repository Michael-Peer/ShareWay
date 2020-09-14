package com.example.shareway.listeners

import com.example.shareway.models.Article

interface OnCategoryClickListener {
    fun onCategoryClick(position: Int)
    fun onTextClick(position: Int)
    fun onCheckIconClick(newCategoryName: String, position: Int)
}

interface OnArticleClickListener {
    fun onArticleClick(position: Int)
    fun onLongArticleClick(position: Int)
}
