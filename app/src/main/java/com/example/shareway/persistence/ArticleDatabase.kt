package com.example.shareway.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shareway.models.Article
import com.example.shareway.models.Category


private const val DB_NAME = "articles.db"

@Database(entities = [Article::class, Category::class], version = 6)
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