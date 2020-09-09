package com.example.shareway.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.shareway.databinding.CategoryListItemBinding
import com.example.shareway.listeners.OnCategoryClickListener
import com.example.shareway.listeners.OnStartDragListener
import com.example.shareway.listeners.RowMovesListener
import com.example.shareway.models.Category
import com.example.shareway.viewholders.ArticleListViewHolder
import com.example.shareway.viewholders.CategoryListViewHolder
import kotlinx.android.synthetic.main.category_list_item.view.*
import java.util.*

class CategoryListAdapter(
    private val onCategoryClickListener: OnCategoryClickListener,
    private val startDragListener: OnStartDragListener
) :
    ListAdapter<Category, CategoryListViewHolder>(DIFF_CALLBACK), RowMovesListener {

    private var mutableCopyList: List<Category> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val view =
//            LayoutInflater.from(parent.context).inflate(R.layout.category_list_item, parent, false)
            CategoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryListViewHolder(view, onCategoryClickListener)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        val categoryItem = getItem(position)
        holder.bind(categoryItem)

        holder.itemView.textView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                this.startDragListener.onStartDrag(holder)
            }
            return@setOnTouchListener true
        }
    }

    fun getCurrentDomainName(position: Int): String? {
        return if (currentList.isNotEmpty()) {
            val item = getItem(position)
            item.categoryName
        } else {
            null
        }
    }

    fun modifyList(list: List<Category>) {
        mutableCopyList = list
        submitList(list)
    }


    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Category> =
            object : DiffUtil.ItemCallback<Category>() {
                override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
                    return oldItem.categoryName == newItem.categoryName
                }

                override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
                    return oldItem == newItem
                }
            }
    }


    /**
     *
     * Initially, I wrote  Collections.swap(currentList, i, i + 1) and I got a crash.
     * the prboem was the "currentList" that came from diff util is a "regular" list and not mutable list.
     * what I did to solve this problem is instead of submitting the list with direct submitList I creared a function called modifyList.
     * modifyList first save the list to mutableList and only then call submit liste
     * And inside onRowMoved I swapped the ref list, not the original one.
     *
     *
     * **/
    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
//                Collections.swap(currentList, i, i + 1)
                Collections.swap(mutableCopyList, i, i + 1)

            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
//                Collections.swap(currentList, i, i - 1)
                Collections.swap(mutableCopyList, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
//        submitList(listRef)
    }

    override fun onRowSelected(itemViewHolder: ArticleListViewHolder) {
        TODO("Not yet implemented")
    }

    override fun onRowClear(itemViewHolder: ArticleListViewHolder) {
        TODO("Not yet implemented")
    }
}