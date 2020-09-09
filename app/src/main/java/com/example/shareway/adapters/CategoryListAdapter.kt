package com.example.shareway.adapters

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

class CategoryListAdapter(private val onCategoryClickListener: OnCategoryClickListener,
                          private val startDragListener: OnStartDragListener
) :
    ListAdapter<Category, CategoryListViewHolder>(DIFF_CALLBACK), RowMovesListener {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val view =
//            LayoutInflater.from(parent.context).inflate(R.layout.category_list_item, parent, false)
            CategoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryListViewHolder(view, onCategoryClickListener)
    }

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
     * To solve this question, maybe create a ref list, make it mutahble
     *
     * **/
    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(currentList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(currentList, i, i - 1)
            }
        }
//        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRowSelected(itemViewHolder: ArticleListViewHolder) {
        TODO("Not yet implemented")
    }

    override fun onRowClear(itemViewHolder: ArticleListViewHolder) {
        TODO("Not yet implemented")
    }
}