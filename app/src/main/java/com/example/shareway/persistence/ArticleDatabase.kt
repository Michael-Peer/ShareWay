package com.example.shareway.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.shareway.models.Article
import com.example.shareway.models.Category
import com.example.shareway.utils.converters.Converters


private const val DB_NAME = "articles.db"


/**
 *
 * TODO: Fallback strategy
 *
 * **/
@Database(entities = [Article::class, Category::class], version = 17)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao

    abstract fun categoryDao(): CategoryDao

    companion object {
        fun dbInstance(context: Context): ArticleDatabase {
            return Room.databaseBuilder(context, ArticleDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}