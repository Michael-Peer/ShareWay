package com.example.shareway.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.shareway.models.Category
import com.example.shareway.repositories.CategoryRepository
import com.example.shareway.utils.Constants
import com.example.shareway.viewstates.CategoriesViewState
import com.example.shareway.workers.WorkOnArticleWorker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class CategoriesViewModel constructor(
    private val categoryRepository: CategoryRepository,
    private val context: Context
) : ViewModel() {

    companion object {
        private const val TAG = "CategoriesViewModel"
    }

//    val _articles: MutableLiveData<List<Article>> = articleRepository.getAllArticles().asLiveData()\


//    val articles: LiveData<List<Article>> = articleRepository.getAllArticles().asLiveData()

//    val categories: LiveData<List<Category>> = articleRepository.getAllCategories().asLiveData()

//    private val _viewState: MutableLiveData<CategoriesViewState> = MutableLiveData<CategoriesViewState>()
////    val viewState: LiveData<CategoriesViewState>
////        get() = _viewState

    val viewState: LiveData<CategoriesViewState> =
        categoryRepository.getAllCategories().asLiveData()


//    val articles: LiveData<List<Article>> = liveData {
//
//    }


    fun manipulateArticleUrl(url: String) {


        /**
         *
         * Create request
         *
         * **/
        val workWorker: OneTimeWorkRequest.Builder =
            OneTimeWorkRequestBuilder<WorkOnArticleWorker>()

        /**
         *
         *
         * Set data
         *
         * **/
        val data = Data.Builder()
        data.putString(Constants.URL_KEY, url)
        workWorker.setInputData(data.build())

        /**
         *
         * Submit request
         *
         * **/
        WorkManager
            .getInstance(context)
            .enqueue(workWorker.build())
    }

    fun saveItemsPosition(items: List<Category>) {
        viewModelScope.launch {
            categoryRepository.saveItemsPosition(items)
        }
    }

    fun saveNewCategoryName(
        newCategoryName: String,
        currentCategory: Category
    ) {
        viewModelScope.launch {
            categoryRepository.saveNewCategoryName(newCategoryName, currentCategory)
        }
    }

    fun getCategoriesByName() {

        Log.d(TAG, "getCategoriesByName: ")
//        categoryRepository.getCatergoriesByName()
    }

    fun resetToOriginalName(category: Category, categories: List<Category>) {
        viewModelScope.launch {
            categoryRepository.resetToOriginalName(category, categories)

        }
    }

    fun onMarkAsRead(category: Category) {
        viewModelScope.launch {
            categoryRepository.markCategoryAsRead(category)

        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)

        }
    }


}

//@ExperimentalCoroutinesApi
//class CategoriesViewModel constructor(
//    private val categoryRepository: CategoryRepository,
//    private val context: Context
//) : ViewModel() {
//
//    companion object {
//        private const val TAG = "CategoriesViewModel"
//    }
//
////    val _articles: MutableLiveData<List<Article>> = articleRepository.getAllArticles().asLiveData()\
//
//
////    val articles: LiveData<List<Article>> = articleRepository.getAllArticles().asLiveData()
//
////    val categories: LiveData<List<Category>> = articleRepository.getAllCategories().asLiveData()
//
////    private val _viewState: MutableLiveData<CategoriesViewState> = MutableLiveData<CategoriesViewState>()
//////    val viewState: LiveData<CategoriesViewState>
//////        get() = _viewState
//
//    val viewState: LiveData<CategoriesViewState> =
//        categoryRepository.getAllCategories().asLiveData()
//
//
////    val articles: LiveData<List<Article>> = liveData {
////
////    }
//
//
//    fun manipulateArticleUrl(url: String) {
//
//
//        /**
//         *
//         * Create request
//         *
//         * **/
//        val workWorker: OneTimeWorkRequest.Builder =
//            OneTimeWorkRequestBuilder<WorkOnArticleWorker>()
//
//        /**
//         *
//         *
//         * Set data
//         *
//         * **/
//        val data = Data.Builder()
//        data.putString(Constants.URL_KEY, url)
//        workWorker.setInputData(data.build())
//
//        /**
//         *
//         * Submit request
//         *
//         * **/
//        WorkManager
//            .getInstance(context)
//            .enqueue(workWorker.build())
//    }
//
//    fun saveItemsPosition(items: List<Category>) {
//        categoryRepository.saveItemsPosition(items)
//    }
//
//    fun saveNewCategoryName(
//        newCategoryName: String,
//        currentCategory: Category
//    ) {
//        categoryRepository.saveNewCategoryName(newCategoryName, currentCategory)
//    }
//
//    fun getCategoriesByName() {
//        Log.d(TAG, "getCategoriesByName: ")
////        categoryRepository.getCatergoriesByName()
//    }
//
//    fun resetToOriginalName(category: Category, categories: List<Category>) {
//        categoryRepository.resetToOriginalName(category, categories)
//    }
//
//    fun onMarkAsRead(category: Category) {
//        categoryRepository.markCategoryAsRead(category)
//    }
//
//    fun deleteCategory(category: Category) {
//        categoryRepository.deleteCategory(category)
//    }
//
//
//}