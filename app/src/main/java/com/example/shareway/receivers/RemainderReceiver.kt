package com.example.shareway.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.shareway.MainApp.Companion.NOTF_CHANNEL_ID_REMINDER
import com.example.shareway.R
import com.example.shareway.persistence.ArticleDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.core.KoinComponent
import org.koin.core.inject

class RemainderReceiver() : BroadcastReceiver(), KoinComponent {

    private val articleDao: ArticleDao by inject()

    companion object {
        private const val TAG = "RemainderReceiver"
    }


    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    override fun onReceive(context: Context?, intent: Intent?) {

        val nmc = NotificationManagerCompat.from(context!!)

        Log.d(TAG, "onReceive: ")
        val builder = NotificationCompat.Builder(context!!, NOTF_CHANNEL_ID_REMINDER)
            .setSmallIcon(R.drawable.ic_baseline_check_24)
            .setContentTitle("You have ")
            .setContentText("article to read!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        nmc.notify(1, builder)

        intent?.let {
            val articlrUrl = it.getStringExtra("articleUrl")
            Log.d(TAG, "onReceive: article url = $articlrUrl")
//            articleDao.cancelReminder()

        }
    }

}