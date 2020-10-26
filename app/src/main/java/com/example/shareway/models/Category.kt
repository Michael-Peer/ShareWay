package com.example.shareway.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


/**
 *
 * [originalCategoryName] - original name we extract from the web
 * [newCategoryName] - category name after user edit
 *
 * **/
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey
    val originalCategoryName: String,
    /**
     *
     * At first I set the [newCategoryName] to "" initially, indicating that this string is empty.
     * When I added the filter categories feature, I needed to do search on the [newCategoryName] and not the [originalCategoryName], but the [newCategoryName] was set the empty string at the first time
     * So I set [newCategoryName] to  [originalCategoryName] so I can filter the categories
     *
     *
     * **/
//    var newCategoryName: String? = ""
    var newCategoryName: String = originalCategoryName,

    /**
     *
     * Start with 1, cause when new category is created there is already 1 article
     *
     * **/
//    var numberOfArticles: Int = 1

    /**
     *
     * base url of category
     *
     *
     * **/
    val baseUrl: String,

    /**
     *
     *
     * Website favicon
     *
     * **/
    var faviconUrl: String? = null

    /**
     *
     *
     * Is item clickable when in edit mode
     *
     *
     * **/

) {
    /**
     *
     * The [@Ignore] room annotation SHOULDN'T be inside the constructor.
     * Ref: https://github.com/android/architecture-components-samples/issues/421
     *
     * This field determine if the category is currently clickable or not
     *
     * **/
    @Ignore
    var isClickable: Boolean = true

}

