package com.example.shareway.workers

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.shareway.models.Article
import com.example.shareway.models.Category
import com.example.shareway.persistence.ArticleDao
import com.example.shareway.persistence.CategoryDao
import com.example.shareway.utils.Constants
import kotlinx.coroutines.delay
import org.koin.core.KoinComponent
import org.koin.core.inject

class WorkOnArticleWorker(appContext: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(appContext, workerParameters), KoinComponent {

    private val articleDao: ArticleDao by inject()
    private val categoryDao: CategoryDao by inject()

    companion object {
        private const val TAG = "WorkOnArticleWorker"
    }

    override suspend fun doWork(): Result {

        var websiteName: String? = null
        //get data
        val url = inputData.getString(Constants.URL_KEY)

        url?.let { websiteName = extractDomainNameFromUri(it) } ?: return Result.failure()

        websiteName?.let {
            try {


                fakeDelay(it, url)
                checkCategoriesAndInsert(it)
                insertToDB(url, it)
                Log.d(TAG, "articleDao hash code: ${articleDao.hashCode()}")
                Log.d(TAG, "categoryDao hash code: ${categoryDao.hashCode()}")



                val outputData = Data.Builder()
                    .putString(Constants.DOMAIN_NAME_KEY, websiteName)
                    .build()

//               articleDao.deleteAllArticles()
//                categoryDao.deleteAllCategories()


                return Result.success(outputData)
            } catch (e: Throwable) {
                return Result.failure()
            }
        } ?: return Result.failure()
    }

    private suspend fun fakeDelay(domainName: String, url: String) {
        for (i in 0..2) {
            Log.d(
                TAG,
                "WorkOnArticleWorker: service running, current count : $i --- $domainName "
            )
            Log.d(
                TAG,
                "WorkOnArticleWorker: service running, current count : $i --- $url "
            )
            delay(1000)
        }
    }

//    private suspend fun checkCategoriesAndInsert(domainName: String) {
//        categoryDao.insertCategory(
//            Category(
//                categoryName = domainName
//            )
//        )
//    }

    private suspend fun checkCategoriesAndInsert(domainName: String) {
        categoryDao.insertCategory(
            Category(
                originalCategoryName = domainName
            )
        )
    }



    private suspend fun insertToDB(url: String, domainName: String) {
        articleDao.insertArticle(
            article = Article(
                url = url,
                domainName = domainName
            )
        )
    }

    private fun extractDomainNameFromUri(url: String): String? {
        val uri: Uri = url.toUri()
        val uriHost = uri.host
        var websiteName: String? = null
        uriHost?.let { host ->
            websiteName = getWebsiteNameFromUri(host)
        }
        return websiteName
    }

    private fun getWebsiteNameFromUri(host: String): String {
        val cutString = host.substringAfter(".")
        return cutString.substringBefore(".").capitalize()
    }


}