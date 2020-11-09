package com.example.shareway.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.shareway.MainApp.Companion.NOTF_CHANNEL_ID_REMINDER
import com.example.shareway.R
import com.example.shareway.persistence.ArticleDao
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.random.Random

class RemainderReceiver() : BroadcastReceiver(), KoinComponent {

    private val articleDao: ArticleDao by inject()

    companion object {
        private const val TAG = "RemainderReceiver"
    }


    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    override fun onReceive(context: Context?, intent: Intent?) {

        //TODO: Handle nulls


        context?.let { mcontext ->
            intent?.let { mintent ->

                val nmc = NotificationManagerCompat.from(mcontext)


                val bundle = Bundle()
                bundle.putString("articlesUrl", mintent.getStringExtra("articleUrl"))
                val pintent = NavDeepLinkBuilder(mcontext)
                    .setGraph(R.navigation.nav_graph)
                    .setDestination(R.id.articleDetailFragment)
                    .setArguments(bundle)
                    .createPendingIntent()


                Log.d(TAG, "onReceive: ")
                val builder = NotificationCompat.Builder(mcontext, NOTF_CHANNEL_ID_REMINDER)
                    .setSmallIcon(R.drawable.ic_baseline_article_24)
                    .setContentTitle("You set reminder for")
                    .setContentText(mintent.getStringExtra("articleTitle") ?: mintent.getStringExtra("articleUrl") ?: "Click to read!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pintent)
                    .setAutoCancel(true)
                    .build()

                nmc.notify(Random.nextInt(), builder)

                /**
                 *
                 * After alarm manager trigger the notification, we want to set back the reminder to null
                 *
                 * **/
                val articerUrl = mintent.getStringExtra("articleUrl")
                Log.d(TAG, "onReceive: article url = $articerUrl")
                articerUrl?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        articleDao.cancelReminder(it)
                    }
                }


            }
        }

    }

}