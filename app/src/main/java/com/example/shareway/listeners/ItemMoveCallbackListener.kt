package com.example.shareway.listeners

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shareway.adapters.CategoryListAdapter
import com.example.shareway.viewholders.ArticleListViewHolder
import com.example.shareway.viewholders.CategoryListViewHolder

class ItemMoveCallbackListener(val adapter: CategoryListAdapter) : ItemTouchHelper.Callback() {

    companion object{
        private const val TAG = "ItemMoveCallbackListene"
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val flags =
            ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.END or ItemTouchHelper.START
        return makeMovementFlags(flags, 0)
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onRowMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState!=ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder is CategoryListViewHolder) {
                adapter.onRowSelected(viewHolder)
            }
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder is CategoryListViewHolder) {
            adapter.onRowClear(viewHolder)
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        Log.d(TAG, "onSwiped: ")
    }
}