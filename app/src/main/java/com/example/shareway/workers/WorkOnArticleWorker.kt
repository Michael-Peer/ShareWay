package com.example.shareway.workers

import android.content.Context
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import androidx.core.net.toUri
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.shareway.utils.Constants

class WorkOnArticleWorker(appContext: Context, workerParameters: WorkerParameters) :
    Worker(appContext, workerParameters) {

    companion object {
        private const val TAG = "WorkOnArticleWorker"
    }

    override fun doWork(): Result {

        var websiteName: String? = null
        //get data
        val url = inputData.getString(Constants.URL_KEY)

        url?.let { websiteName = extractDomainNameFromUri(it) } ?: return Result.failure()

        websiteName?.let {
            try {
                for (i in 0..10) {
                    Log.d(
                        TAG,
                        "WorkOnArticleWorker: service running, current count : $i --- $websiteName "
                    )
                    SystemClock.sleep(1000)
                }
//                val article = Article(domainName = websiteName)

                val outputData = Data.Builder()

                    .putString(Constants.DOMAIN_NAME_KEY, websiteName)
                    .build()

                return Result.success(outputData)
            } catch (e: Throwable) {
                return Result.failure()
            }
        } ?: return Result.failure()
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