package com.example.shareway.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.shareway.repositories.CategoryRepository
import com.example.shareway.utils.Constants
import com.example.shareway.viewstates.CategoriesViewState
import com.example.shareway.workers.WorkOnArticleWorker
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class CategoriesViewModel constructor(
    private val categoryRepository: CategoryRepository,
    private val context: Context
) : ViewModel() {

//    val _articles: MutableLiveData<List<Article>> = articleRepository.getAllArticles().asLiveData()\


//    val articles: LiveData<List<Article>> = articleRepository.getAllArticles().asLiveData()

//    val categories: LiveData<List<Category>> = articleRepository.getAllCategories().asLiveData()

//    private val _viewState: MutableLiveData<CategoriesViewState> = MutableLiveData<CategoriesViewState>()
////    val viewState: LiveData<CategoriesViewState>
////        get() = _viewState

    val viewState: LiveData<CategoriesViewState> = categoryRepository.getAllCategories().asLiveData()


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


}