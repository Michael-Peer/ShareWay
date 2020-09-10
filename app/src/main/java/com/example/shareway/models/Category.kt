package com.example.shareway.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey
    val originalCategoryName: String,
    var newCategoryName: String? = ""
)