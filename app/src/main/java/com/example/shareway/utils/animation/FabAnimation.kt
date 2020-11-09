package com.example.shareway.utils.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.util.Log
import android.view.View
import java.time.Duration

object FabAnimation {

    private const val TAG = "FabAnimation"

    fun rotateFab(view: View, isFabOpen: Boolean):  Boolean {
        view.animate()
            .rotation(if (isFabOpen) 665f else 0f)
            .setDuration(800)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    Log.d(TAG, "onAnimationEnd: ")
                }
            })
        return isFabOpen
    }

    fun showFabMenu(view: View, duration: Long) {
        view.visibility = View.VISIBLE
        view.alpha = 0f
        view.animate()
            .setDuration(duration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {

                    super.onAnimationEnd(animation)
                }
            })
            .alpha(1f)
            .start()
    }

    fun hideFabMenu(view: View, duration: Long) {
        view.animate()
            .setDuration(duration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    view.visibility = View.GONE

                    super.onAnimationEnd(animation)
                }
            }).alpha(0f)
            .start()

    }


    fun initFabMenu(view: View) {
        view.visibility = View.GONE
    }




}

