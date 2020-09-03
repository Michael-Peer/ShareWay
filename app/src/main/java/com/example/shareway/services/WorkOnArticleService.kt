package com.example.shareway.services

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.core.app.JobIntentService

class WorkOnArticleService : JobIntentService() {

    companion object {
        private const val JOB_ID = 100
        private const val TAG = "WorkOnArticleService"

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, WorkOnArticleService::class.java, JOB_ID, work)
        }
    }


    override fun onHandleWork(intent: Intent) {

        for (i in 0..15) {
            Log.d(TAG, "onHandleWork: service running, current count : $i ")
            SystemClock.sleep(2000)
        }
    }


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    /**
     *
     * Default true
     *
     * **/
    override fun onStopCurrentWork(): Boolean {
        Log.d(TAG, "onStopCurrentWork: ")
        return super.onStopCurrentWork()
    }

}