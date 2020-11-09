package com.example.shareway.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.time.Instant
import java.util.*


@Parcelize
@Entity(tableName = "notes",
foreignKeys = [ForeignKey(
    entity = Article::class,
    parentColumns = arrayOf("url"),
    childColumns = arrayOf("articleUrl"),
    onDelete =  ForeignKey.CASCADE
)]
    )
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val articleUrl: String,
    val title: String,
    val content: String,
    val dateAdded: Instant = Instant.now()
    ) : Parcelable {

}