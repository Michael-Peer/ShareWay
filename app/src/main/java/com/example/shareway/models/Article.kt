package com.example.shareway.models

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDateTime

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
    var title: String? = null,
    var articleImage: String? = null,
    /**
     *
     * If there is no article image(for example twitter), I want to fallback to the categoryImage
     *
     * **/
    var defaultImage: String? = null,

    /**
     *
     * Ref answer why I'm using here Instant and not LocalDateTime - https://stackoverflow.com/questions/54991260/getting-java-time-datetimeexception-when-formatting-localdatetime-instance-with
     *
     * **/
    val dateAdded: Instant = Instant.now(),

    /**
     *
     * Indication if user set reminder to this article or not
     *
     **/
    var reminder: Instant? = null


) {
}