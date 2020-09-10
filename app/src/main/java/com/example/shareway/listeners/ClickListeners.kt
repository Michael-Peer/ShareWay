package com.example.shareway.listeners

interface OnCategoryClickListener {
    fun onCategoryClick(position: Int)
    fun onTextClick(position: Int)
    fun onCheckIconClick(newCategoryName: String, position: Int)
}

interface OnArticleClickListener {
    fun onArticleClick(position: Int)
}
