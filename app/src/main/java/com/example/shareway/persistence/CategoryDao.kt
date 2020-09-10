package com.example.shareway.persistence

import androidx.room.*
import com.example.shareway.models.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE originalCategoryName = :categoryName")
    fun getCategory(categoryName: String): Flow<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveItemsPosition(category: Category)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()
}