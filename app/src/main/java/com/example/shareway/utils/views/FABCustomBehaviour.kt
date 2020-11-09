package com.example.shareway.utils.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FABCustomBehaviour(context: Context, attrs: AttributeSet) : FloatingActionButton.Behavior(context, attrs) {


    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        Log.d(TAG, "onNestedScroll: ")

        return axes == ViewCompat.SCROLL_AXIS_VERTICAL ||

                 super.onStartNestedScroll(
            coordinatorLayout,
            child,
            directTargetChild,
            target,
            axes,
            type
        )
    }

companion object{
    private const val TAG = "FABCustomBehaviour"
}

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )

        Log.d(TAG, "onNestedScroll: ")

        if (dyConsumed > 0 && child.visibility == View.VISIBLE) {
            child.hide();

        } else if (dyConsumed < 0 && child.visibility != View.VISIBLE) {
            child.show();
        }
    }

}


