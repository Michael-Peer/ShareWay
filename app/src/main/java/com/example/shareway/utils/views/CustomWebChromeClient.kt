package com.example.shareway.utils.views

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.widget.FrameLayout


 open class CustomWebChromeClient(private val context: Activity) : WebChromeClient() {


    companion object {
        private const val FULL_SCREEN_SETTING = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE
    }

    private var customView: View? = null
    private var customViewCallback: CustomViewCallback? = null
    private var originalOrientation = 0
    private var originalSystemUiVisibility = 0

    override fun onHideCustomView() {
        (context.window.decorView as FrameLayout).removeView(customView)
        context.window.decorView.systemUiVisibility = originalSystemUiVisibility
        context.requestedOrientation = originalOrientation
        customViewCallback?.onCustomViewHidden()
        context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
    }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        super.onShowCustomView(view, callback)
        customView?.let {
            onHideCustomView()
        } ?: showView(view, callback)
    }

    private fun showView(view: View?, callback: CustomViewCallback?) {
        customView = view
        originalSystemUiVisibility = context.window.decorView.systemUiVisibility
        originalOrientation = context.requestedOrientation
        customViewCallback = callback
        (context.window.decorView as FrameLayout)
            .addView(
                customView,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        context.window.decorView.systemUiVisibility = FULL_SCREEN_SETTING
    }


}