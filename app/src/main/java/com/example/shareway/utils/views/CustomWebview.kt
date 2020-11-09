package com.example.shareway.utils.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.webkit.WebView


/**
 *
 * In order to support older API's(specifically API's under 23(Android.M)), I needed to create custom [WebView] to detect scroll changes.
 * The scroll listener used for determine if to show/hide fab/s on scroll down/up.
 *
 * First we need [@JvmOverloads] annotation for constr overloading, ref - https://proandroiddev.com/misconception-about-kotlin-jvmoverloads-for-android-view-creation-cb88f432e1fe [@jvmoverloads kotlin]
 *
 * Next I created interface to talk we the fragment and passing [onScrollDown], [onScrollUp] and [onScroll] functions
 *
 * In the fragment I'll check for API level and decide with listener should I use
 *
 *
 *
 * **/


class CustomWebview @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    WebView(context, attrs) {

    companion object {
        private const val TAG = "CustomWebview"
    }

    var scrollListener: WebViewScrollListener? = null


    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
//        Log.d(TAG, "onScrollChanged: ")
        when {
            t > oldt -> {
                scrollListener?.onScrollDown()
                Log.d(TAG, "onScrollChanged: ")
            }
            t < oldt -> {
                scrollListener?.onScrollUp()
                Log.d(TAG, "onScrollChanged: ")
            }
        }
        scrollListener?.onScroll(l, t, oldl, oldt)
    }


    interface WebViewScrollListener {
        fun onScroll(scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int)

        fun onScrollDown()

        fun onScrollUp()
    }
}