package com.example.shareway.models

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * \
 * [url] - url of the article
 * [domainName] - category name
 * [alreadyRead] - if user already read the article
 *
 * **/

@Entity(tableName = "articles")
data class Article constructor(
    @PrimaryKey
    val url: String,
    val domainName: String,
    var alreadyRead: Boolean = false,
    val dateAdded: Instant = Instant.now()
) {
}