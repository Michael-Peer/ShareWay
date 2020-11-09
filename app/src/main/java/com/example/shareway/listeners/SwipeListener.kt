package com.example.shareway.listeners

import androidx.recyclerview.widget.RecyclerView
import com.example.shareway.models.Article

interface OnSwipeListener {

    fun onSwipeToDelete(position: Int, itemToDelete: Article)
    fun onSwipeToAlreadyRead(position: Int, alreadyRead: Boolean)

}

