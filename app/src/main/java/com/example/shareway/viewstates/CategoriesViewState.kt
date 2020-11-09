package com.example.shareway.viewstates

import com.example.shareway.models.Category
import com.example.shareway.utils.MessageType
import com.example.shareway.utils.UIComponentType

sealed class CategoriesViewState {
    object Loading : CategoriesViewState()

    data class Error(
        val errorMessage: String,
        val messageType: UIComponentType
    ) : CategoriesViewState()

    data class CategoryList(
        val categories: List<Category>
    ) : CategoriesViewState()
}