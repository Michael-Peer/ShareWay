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
import org.jsoup.nodes.Document
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.net.URL

class WorkOnArticleWorker(appContext: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(appContext, workerParameters), KoinComponent {

    private val articleDao: ArticleDao by inject()
    private val categoryDao: CategoryDao by inject()

    var articleTitle: String? = null

    var url: String? = null
    var articlePath: String? = null
    var articleScheme: String? = null

    companion object {
        private const val TAG = "WorkOnArticleWorker"
    }

    override suspend fun doWork(): Result {

        var websiteName: String? = null

        //full article url e.g www.google.com/some/deep/nav - we get this from then intent, pass to viewModel straight to the worker
         url = inputData.getString(Constants.URL_KEY)

        url?.let { websiteName = extractDomainNameFromUri(it) } ?: return Result.failure()
        //don't need check for null, if it was null we already returning Result.failure()

        websiteName?.let {
            try {
                /**
                 *
                 * here I'm passing the host(base) url, cause there are cases when it give me specific page images
                 *
                 * TODO: Run only if there is no category already
                 * **/
//                val faviconUrl = extractFaviconFromUri(getUrlHost(url) ?: url)
                Log.d(TAG, "doWork: before favicon")
                val faviconUrl = extractFaviconFromUri(getFullBaseUrl(url!!)?: url!!)
                Log.d(TAG, "doWork: after favicon")

                //get images for article
                val image = extractArticleImage(url!!)

//                fakeDelay(it, url)
                checkCategoriesAndInsert(it, url!!, faviconUrl)
                insertToDB(url!!, it, faviconUrl)
                Log.d(TAG, "articleDao hash code: ${articleDao.hashCode()}")
                Log.d(TAG, "categoryDao hash code: ${categoryDao.hashCode()}")


                val outputData = Data.Builder()
                    .putString(Constants.DOMAIN_NAME_KEY, websiteName)
                    .build()

//               articleDao.deleteAllArticles()
//                categoryDao.deleteAllCategories()


                return Result.success(outputData)
            } catch (e: Throwable) {
                Log.d(TAG, "doWork: $e")
                return Result.failure()
            }
        } ?: return Result.failure()
    }

    private fun extractArticleImage(url: String): String? {
        val doc = Jsoup.connect(url).get()

        //first try to fetch ogImage
        articleTitle = doc.title()
        val title = doc.getElementById("title")
        val content = doc.getElementById("content")


//        //TODO: first find the og image, than than the others
//        val images = doc.select("img[src~=(?i)\\.(jpe?g)]")
//
//        for (image in images) {
//            Log.d(
//                TAG, "extractArticleImage: Source: ${image.attr("src")}\n" +
//                        "height: ${image.attr("height")}\n" +
//                        "width: ${image.attr("width")}\n" +
//                        "alt text: ${image.attr("alt")}"
//            )
//        }
//
//
//        Log.d(TAG, "extractFaviconFromUri: TITLE ${doc.title()}")

        Log.d(TAG, "extractFaviconFromUri: empty")
        val metaOgImage = doc.select("meta[property=og:image]").first();
        Log.d(TAG, "extractFaviconFromUri: Fail")
        Log.d(TAG, "extractFaviconFromUri: $metaOgImage")
        val ogImageAttr: String? = metaOgImage?.attr("content")
        Log.d(TAG, "extractFaviconFromUri: $ogImageAttr")

        return ogImageAttr
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


        checkForArticleTag(doc)




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

    private fun checkForArticleTag(doc: Document) {
        val e = doc.select("div")
        val ea = doc.getElementsByTag("div")

        for (paragraph in doc.select("article[itemprop=articleBody]")) {
            Log.d(TAG, "checkForArticleTag: ")
            Log.d(TAG, "checkForArticleTag: $paragraph")
        }

        Log.d(TAG, "checkForArticleTag: ${e.size} ${ea.size}")
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


    private suspend fun insertToDB(
        url: String,
        domainName: String,
        faviconUrl: String?
    ) {

//                url = url,
//                domainName = domainName
//            )
//        ).toInt()

        articleDao.insertArticle(
            article = Article(
                url = url,
                domainName = domainName,
                title = articleTitle,
                articleImage = extractArticleImage(url),
                defaultImage = faviconUrl
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

    /**
     *
     * Extracting name from given url.
     * for example www.google.com turn to google.
     *
     * **/
    private fun extractDomainNameFromUri(url: String): String? {
        val uriHost = getUrlHost(url) //return www.example.com
        var websiteName: String? = null
        uriHost?.let { host ->
            websiteName = getWebsiteNameFromUri(host)
        }
        return websiteName
    }

    /**
     *
     *
     * www.google.com/some/thing/weird2321 ----> www.google.com
     *
     * **/
    private fun getUrlHost(url: String): String? {
        val uri: Uri = url.toUri()
        Log.d(TAG, "getUrlHost: $uri")  // www.something.com/Mobile/Article/20-21/1,1,1,52549/370975.html
        Log.d(TAG, "getUrlHost: ${uri.path}") // /Mobile/Article/20-21/1,1,1,52549/370975.html
        Log.d(TAG, "getUrlHost: ${uri.scheme}") //https/http

        articlePath = uri.path
        articleScheme = uri.scheme
        return uri.host
    }

    //adds protocol to start of url
    private fun getFullBaseUrl(url: String): String? {
        var webUrl = url
//        val hasTextBeforeUr
        Log.d(TAG, "getFullBaseUrl: Before $url ")
        val index = url.indexOf("http")
        Log.d(TAG, "getFullBaseUrl: index $index")
        if (index != 0){
            webUrl = url.substring(index)
            this.url = webUrl
        }
//        val str = url.substring(index)
//        Log.d(TAG, "getFullBaseUrl: str $str")
//        val indexAfter= str.indexOf("http")
//        Log.d(TAG, "getFullBaseUrl: $indexAfter")
        val base = URL(webUrl)
        Log.d(TAG, "getFullBaseUrl:  after $base")
        return "${base.protocol}://${base.host}"
    }

    /**
     *
     * www.google.com/some/thing/weird2321 ----> google
     *
     * **/
    private fun getWebsiteNameFromUri(host: String): String {
        val domain = InternetDomainName.from(host).topPrivateDomain().toString()
        Log.d(TAG, "getWebsiteNameFromUri: host $host")
        Log.d(TAG, "getWebsiteNameFromUri: domain $domain")
        Log.d(TAG, "getWebsiteNameFromUri: cut domain ${domain.substringBefore(".").capitalize()}")

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