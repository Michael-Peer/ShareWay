package com.example.shareway.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Struct

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey
    val url: String,
    val domainName: String
) {
}