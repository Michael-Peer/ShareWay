package com.example.shareway.repositories

import android.util.Log
import com.example.shareway.models.Article
import com.example.shareway.models.Category
import com.example.shareway.persistence.ArticleDao
import com.example.shareway.persistence.CategoryDao
import com.example.shareway.utils.UIComponentType
import com.example.shareway.viewstates.CategoriesViewState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class CategoryRepository(
    private val articleDao: ArticleDao,
    private val categoryDao: CategoryDao
) {
    companion object {
        private const val TAG = "ArticleRepository"
    }

    fun getAllArticles(): Flow<List<Article>> {
        Log.d(TAG, "articleDao hascode: ${articleDao.hashCode()}")
        Log.d(TAG, "categoryDao hashcode: ${categoryDao.hashCode()}")
        return articleDao.getAllArticles()
    }

//    fun getAllCategories(): Flow<List<Category>> {
//        Log.d(TAG, "dao hascode: ${categoryDao.hashCode()}")
//        return categoryDao.getAllCategories()
//    }

//    fun getAllCategories(): Flow<List<Category>> {
//        throw Exception("dadadadaada")
//        return categoryDao.getAllCategories()
//    }

//    fun getAllCategories(): DataState<Flow<List<Article>>> {
//        try {
//            val call = articleDao.getAllArticles()
//            return DataState(
//                data = call
//            )
//        }
//    }

    //    fun getAllCategories(): Flow<CategoriesViewState> = flow {
    @ExperimentalCoroutinesApi
    fun getAllCategories(): Flow<CategoriesViewState> = flow {

        Log.d(TAG, "articleDao hascode: ${articleDao.hashCode()}")
        Log.d(TAG, "categoryDao hashcode: ${categoryDao.hashCode()}")

        emit(CategoriesViewState.Loading)

        delay(4000)

        try {
            categoryDao.getAllCategories().collect {
                emit(
                    CategoriesViewState.CategoryList(
                        categories = it
                    )
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, "getAllCategories: $e")
            emit(
                CategoriesViewState.Error(
                    errorMessage = "Unknown Error",
                    messageType = UIComponentType.Toast
                )
            )
        }


//        categoryDao.getAllCategories().collect {
//            emit(
//                CategoriesViewState.CategoryList(
//                    categories = it
//                )
//            )
//        }


//        emit(object : CacheResponseHandler<CategoriesViewState, List<Category>>(
//            response = cacheResult
//        ) {
//            override fun handleError(
//                errorMessage: String,
//                uiComponentType: UIComponentType.Dialog
//            ): CategoriesViewState {
//                return CategoriesViewState.Error(
//                    errorMessage = errorMessage,
//                    messageType = uiComponentType
//                )
//            }
//
//            override suspend fun handleSuccess(resultObj: List<Category>): CategoriesViewState {
//                return CategoriesViewState.CategoryList(
//                    categories = resultObj
//                )
//
//
//
//            }
//
//        }.getResult())


    }.flowOn(Dispatchers.IO)

    fun saveItemsPosition(items: List<Category>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (item in items) {
                categoryDao.saveItemsPosition(item)
            }
        }

    }

    fun saveNewCategoryName(newCategoryName: String, currentCategory: Category) {
        currentCategory.newCategoryName = newCategoryName
        CoroutineScope(Dispatchers.IO).launch {
            categoryDao.insertCategory(category = currentCategory)
        }
    }

//    fun getCatergoriesByName() = flow {
//        Log.d(TAG, "articleDao hascode: ${articleDao.hashCode()}")
//        Log.d(TAG, "categoryDao hashcode: ${categoryDao.hashCode()}")
//
//        emit(CategoriesViewState.Loading)
//
//        delay(4000)
//
//        try {
//            categoryDao.getAllCategories().collect {
//                emit(
//                    CategoriesViewState.CategoryList(
//                        categories = it
//                    )
//                )
//            }
//        } catch (e: Exception) {
//            Log.d(TAG, "getAllCategories: $e")
//            emit(
//                CategoriesViewState.Error(
//                    errorMessage = "Unknown Error",
//                    messageType = UIComponentType.Toast
//                )
//            )
//        }
//    }


}