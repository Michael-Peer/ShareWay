package com.example.shareway.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * \
 * [url] - url of the article
 * [domainName] - category name
 * [alreadyRead] - if user already read the article
 *
 * **/

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey
    val url: String,
    val domainName: String,
    var alreadyRead: Boolean = false
) {
}