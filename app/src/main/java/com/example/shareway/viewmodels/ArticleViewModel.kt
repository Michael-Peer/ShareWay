package com.example.shareway.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.shareway.utils.Constants
import com.example.shareway.workers.WorkOnArticleWorker

class ArticleViewModel(application: Application) : AndroidViewModel(application) {

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
            .getInstance(getApplication())
            .enqueue(workWorker.build())
    }

}