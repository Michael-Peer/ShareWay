package com.example.shareway.models


/**
 *
 * Hold list of categories as set
 * set because the need is to have only one category per name
 *
 * **/

data class Categories(
    val categories: Set<Category>
)