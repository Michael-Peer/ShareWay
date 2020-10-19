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

//    @Query("SELECT * FROM categories WHERE originalCategoryName = :categoryName")
//    fun getCategoryWithoutFlow(categoryName: String): Category

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveItemsPosition(category: Category)

    @Query("UPDATE categories SET newCategoryName = originalCategoryName WHERE originalCategoryName in (:originalCategoryName)")
    suspend fun resetToOriginalCategoryName(originalCategoryName: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()

//    @Update
//    fun updateCategory(category: Category)


//    @Query("UPDATE categories SET numberOfArticles = numberOfArticles + 1 WHERE originalCategoryName = :articleDomainName")
//    fun incrementAlreadyReadField(articleDomainName: String)
//
//    @Query("UPDATE categories SET numberOfArticles = numberOfArticles - 1  WHERE originalCategoryName = :articleDomainName")
//    fun decrementAlreadyReadField(articleDomainName: String)


}