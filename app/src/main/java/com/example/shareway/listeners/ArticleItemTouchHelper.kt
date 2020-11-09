package com.example.shareway.listeners

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shareway.R
import com.example.shareway.adapters.ArticleListAdapter

class ArticleItemTouchHelper(
    context: Context,
    private val adapter: ArticleListAdapter
) : ItemTouchHelper.Callback() {

    companion object {
        private const val TAG = "ArticleItemTouchHelper"
    }

    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete_24)
    private val checkIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_check_24)
    private var clearcccc = false

    private val intrinsicWidth = deleteIcon!!.intrinsicWidth
    private val intrinsicHeight = deleteIcon!!.intrinsicHeight
    private val background = ColorDrawable()
    private val deleteBackgroundColor: Int = Color.parseColor("#f44336")
    private val alreadyReadBackgroundColor = Color.parseColor("#006400")
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
    private var deleteIconBackgroundColor = ColorDrawable(Color.parseColor("#f44336"))
    private var alreadyReadIconBackgroundColor = ColorDrawable(Color.parseColor("#006400"))


    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {

        /**
         * To disable "swipe" for specific item return 0 here.
         * For example:
         * if (viewHolder?.itemViewType == YourAdapter.SOME_TYPE) return 0
         * if (viewHolder?.adapterPosition == 0) return 0
         */
        val flags = ItemTouchHelper.START or ItemTouchHelper.END


        /**
         *
         * TODO: REMOVE
         *
         * **/
        recyclerView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View?,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                recyclerView.removeOnLayoutChangeListener(this)
                Log.d(TAG, "onLayoutChange: ")
            }

        })

        return makeMovementFlags(0, flags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false // We don't want support moving items up/down
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.START) {
            adapter.onSwipedDelete(viewHolder.adapterPosition)
        } else if (direction == ItemTouchHelper.END) {
            adapter.onSwipedAlreadyRead(viewHolder.adapterPosition)
        }
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    /**
     *
     * If multi selection active we don't want to enable swiping
     * **/
    override fun isItemViewSwipeEnabled(): Boolean {
        return !adapter.isMultiSelectionActive()
    }


//    override fun onChildDraw(
//        c: Canvas,
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder,
//        dX: Float,
//        dY: Float,
//        actionState: Int,
//        isCurrentlyActive: Boolean
//    ) {
//        val itemView = viewHolder.itemView
//
//        val deleteIconHeight = (itemView.height - deleteIcon!!.intrinsicHeight) / 2
//        val checkIconHeight = (itemView.height - checkIcon!!.intrinsicHeight) / 2
//
//        Log.d(TAG, "onChildDraw: ACTION STATE: $actionState")
//        Log.d(TAG, "onChildDraw: IS CURRENTLY ACTIVE $isCurrentlyActive")
//        if (!isCurrentlyActive) {
//            clearView(recyclerView, viewHolder)
//        } else {
//            when {
//                /**
//                 *
//                 * Swipe from left
//                 *
//                 * **/
//                dX > 0 -> {
//                    //painting the rectangle bounds
//                    deleteIconBackgroundColor.setBounds(
//                        itemView.left,
//                        itemView.top,
//                        dX.toInt(),
//                        itemView.bottom
//                    )
//                    deleteIcon.setBounds(
//                        itemView.left + deleteIconHeight,
//                        itemView.top + deleteIconHeight,
//                        itemView.left + +deleteIconHeight + deleteIcon.intrinsicWidth,
//                        itemView.bottom - deleteIconHeight
//                    )
//
//                }
//                /**
//                 *
//                 * Swipe from right
//                 *
//                 * **/
//                dX < 0 -> {
//                    alreadyReadIconBackgroundColor.setBounds(
//                        itemView.right + dX.toInt(),
//                        itemView.top,
//                        itemView.right,
//                        itemView.bottom
//                    )
//
//                    checkIcon.setBounds(
//                        itemView.right - checkIconHeight - checkIcon.intrinsicWidth,
//                        itemView.top + checkIconHeight,
//                        itemView.right - checkIconHeight,
//                        itemView.bottom - checkIconHeight
//                    )
//
//                }
//            }
//
//            c.save()
//            if (dX > 0) {
//                c.clipRect(
//                    itemView.left,
//                    itemView.top,
//                    dX.toInt(),
//                    itemView.bottom
//                )
//            } else if (dX < 0) {
//                c.clipRect(
//                    itemView.right + dX.toInt(),
//                    itemView.top,
//                    itemView.right,
//                    itemView.bottom
//                )
//            }
//
//            deleteIconBackgroundColor.draw(c)
//            alreadyReadIconBackgroundColor.draw(c)
//            deleteIcon.draw(c)
//            checkIcon.draw(c)
//
//
//            c.restore()
//        }
//
//        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//    }


//    override fun onChildDraw(
//        c: Canvas,
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder,
//        dX: Float,
//        dY: Float,
//        actionState: Int,
//        isCurrentlyActive: Boolean
//    ) {
//
//        val itemView = viewHolder.itemView
//        val backgroundCornerOffset = 20
//        val iconMargin: Int = (itemView.height - deleteIcon!!.getIntrinsicHeight()) / 2
//        val iconTop: Int =
//            itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
//        val iconBottom: Int = iconTop + deleteIcon.intrinsicHeight
//
//
//        /**
//         *
//         * swipe to the right
//         *
//         * **/
//        if (dX > 0) {
//            val iconLeft: Int = itemView.left + iconMargin + deleteIcon.intrinsicWidth
//            val iconRight = itemView.left + iconMargin
//            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
//
//            background.setBounds(
//                itemView.left, itemView.top,
//                (itemView.left + dX + backgroundCornerOffset).toInt(), itemView.bottom
//            )
//
//            deleteIcon.draw(c)
//        }
//        /**
//         *
//         * swipe to the ledt
//         *
//         * **/
//        else if (dX < 0) {
//
//            val iconLeft = itemView.getRight() - iconMargin - checkIcon!!.intrinsicWidth;
//            val iconRight = itemView.getRight() - iconMargin;
//            checkIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
//
//            background.setBounds(
//                itemView.right + dX.toInt() - backgroundCornerOffset,
//                itemView.top, itemView.right, itemView.bottom
//            )
//
//            checkIcon.draw(c)
//        }
//        /**
//         *
//         * view unswiped
//         *
//         * **/
//        else {
//            background.setBounds(0, 0, 0, 0);
//        }
//
//        background.draw(c)
//
//
//
//
//        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.height


        background.color = deleteBackgroundColor
        background.setBounds(
            itemView.right + dX.toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        background.draw(c)

        val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
        val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
        val deleteIconRight = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + intrinsicHeight
        deleteIcon!!.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteIcon.draw(c)

        val alreadyReadIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val alreadyReadIconMargin = (itemHeight - intrinsicHeight) / 2
        val alreadyReadIconLeft = itemView.left - deleteIconMargin - intrinsicWidth
        val alreadyReadIconRight = itemView.left - deleteIconMargin
        val alreadyReadIconBottom = alreadyReadIconTop + intrinsicHeight
        deleteIcon!!.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteIcon.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {

        super.clearView(recyclerView, viewHolder)
    }


    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}