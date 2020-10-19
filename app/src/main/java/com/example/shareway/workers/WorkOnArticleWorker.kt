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
import com.google.common.net.InternetDomainName
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import org.koin.core.KoinComponent
import org.koin.core.inject

class WorkOnArticleWorker(appContext: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(appContext, workerParameters), KoinComponent {

    private val articleDao: ArticleDao by inject()
    private val categoryDao: CategoryDao by inject()

    private var isOldCategory = false

    companion object {
        private const val TAG = "WorkOnArticleWorker"
    }

    override suspend fun doWork(): Result {

        var websiteName: String? = null
        //get data
        val url = inputData.getString(Constants.URL_KEY)

        url?.let { websiteName = extractDomainNameFromUri(it) } ?: return Result.failure()
        //don't need check for null, if it was null we already returning Result.failure()

        websiteName?.let {
            try {
                val faviconUrl = extractFaviconFromUri(url)
                Log.d(TAG, "doWork: after favicon")

//                fakeDelay(it, url)
                checkCategoriesAndInsert(it, url, faviconUrl)
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


    //just an example TODO: Update code for efficentiy
    /**
     *
     * Handle different scenarios of fetching images from the web(not favicon.ico! favicon become very blurry and not optimize at all)
     *
     * **/
    private fun extractFaviconFromUri(url: String): String? {
        Log.d(TAG, "extractFaviconFromUri: $url")
        val doc = Jsoup.connect(url).get()
        Log.d(TAG, "extractFaviconFromUri: ${doc.title()}")
        val element = doc.head().select("link[rel=apple-touch-icon]")
        Log.d(TAG, "extractFaviconFromUri: $element")
        var attr = element.attr("href")
        Log.d(TAG, "extractFaviconFromUri: $attr")

        /**
         *
         * Completely different url
         *
         * we need to add in the end "https://" because glide can't download without it
         *
         * **/
        if (attr.startsWith("//")) {
            Log.d(TAG, "extractFaviconFromUri: start with //")
            while (attr.startsWith("/")) {
                attr = attr.removePrefix("/")
            }
            Log.d(TAG, "extractFaviconFromUri: attr = $attr")
            return "https://$attr"
        }
        /**
         *
         * If the image url start with "/", we need to check several scenarios.
         * 1)if the base url ends with "/", we need to eliminate the "/" from the start of image url. double slash work on the web, not here for image downloading
         * 2)if image url doesn't start with "/", it's a case where we have fully qualified url and we can just return the image url without adding the base url to it
         * 3)if image url is still empty, we are looking for another element. in that case, og:image
         *
         * **/
        else if (attr.startsWith("/")) {
            Log.d(TAG, "extractFaviconFromUri: start with /")
            if (url.endsWith("/")) {
                Log.d(TAG, "extractFaviconFromUri: url end with /")
                val newAttr = attr.removePrefix("/")
                Log.d(TAG, "extractFaviconFromUri: $newAttr")
                attr = "$url$newAttr"
                Log.d(TAG, "extractFaviconFromUri: $newAttr")
            } else {
                Log.d(TAG, "extractFaviconFromUri: url doesn't end with /")
                attr = "$url$attr"
            }
        }

        if (attr.isEmpty()) {
            Log.d(TAG, "extractFaviconFromUri: empty")
            val metaOgImage = doc.select("meta[property=og:image]").first();
            Log.d(TAG, "extractFaviconFromUri: Fail")
            Log.d(TAG, "extractFaviconFromUri: $metaOgImage")
            val ogImageAttr: String? = metaOgImage?.attr("content")
            Log.d(TAG, "extractFaviconFromUri: $ogImageAttr")
            attr = ogImageAttr
        }
        Log.d(TAG, "extractFaviconFromUri: attr = $attr")
        /**
         *
         * Here I needed to check [isNullOrEmpty] insead of just [isEmpty] because I got a crash when tried to check emptiness on a null object.
         * web ref: facebook.com
         *
         * **/
        return if (attr.isNullOrEmpty()) null else attr
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

    private suspend fun checkCategoriesAndInsert(
        domainName: String,
        url: String,
        faviconUrl: String?
    ) {
//        val insertedCategory = categoryDao.insertCategory(
//            Category(
//                originalCategoryName = domainName
//            )
//        ).toInt()

        categoryDao.insertCategory(
            Category(
                originalCategoryName = domainName,
                baseUrl = getUrlHost(url)!!, //TODO: Handle null
                faviconUrl = faviconUrl
            )
        )
//        val insertedArticle = articleDao.insertArticleWorker(
//            article = Article(

//        if (insertedCategory == -1) {
//            Log.d(TAG, "checkCategoriesAndInsert: OLD ROW")
//            //old row
//            isOldCategory = true
//        }

        Log.d(TAG, "checkCategoriesAndInsert: AFTER INSERTING")
    }


    private suspend fun insertToDB(url: String, domainName: String) {

//                url = url,
//                domainName = domainName
//            )
//        ).toInt()

        articleDao.insertArticle(
            article = Article(
                url = url,
                domainName = domainName
            )
        )

//        if (insertedArticle != -1) {
//            Log.d(TAG, "insertToDB: new article")
//
//            if (isOldCategory) {
//                Log.d(TAG, "insertToDB: old category")
//                categoryDao.incrementAlreadyReadField(articleDomainName = domainName)
//            }
//        }
    }

    private fun extractDomainNameFromUri(url: String): String? {
        val uriHost = getUrlHost(url) //return www.example.com
        Log.d(TAG, "extractDomainNameFromUri: $uriHost")
        var websiteName: String? = null
        uriHost?.let { host ->
            websiteName = getWebsiteNameFromUri(host)
        }
        return websiteName
    }

    private fun getUrlHost(url: String): String? {
        val uri: Uri = url.toUri()
        return uri.host
    }

    private fun getWebsiteNameFromUri(host: String): String {

        val domain = InternetDomainName.from(host).topPrivateDomain().toString()
        Log.d(TAG, "getWebsiteNameFromUri: host $host")
        Log.d(TAG, "getWebsiteNameFromUri: domain $domain")
//        val cutString = host.substringAfter(".")
        return try {
            domain.substringBefore(".").capitalize()
        } catch (e: IllegalArgumentException) {
            //TODO: HANDLE ERROR
            ""
        } catch (e: IllegalStateException) {
            //TODO: HANDLE ERROR
            ""
        }


    }


}