package com.example.shareway.viewholders

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.shareway.databinding.CategoryListItemBinding
import com.example.shareway.listeners.OnCategoryClickListener
import com.example.shareway.models.Category

class CategoryListViewHolder(
    private val binding: CategoryListItemBinding,
    private val onCategoryClickListener: OnCategoryClickListener
) :
    RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(categoryItem: Category?) {
        categoryItem?.let {
            binding.apply {
                textView.text = categoryItem.categoryName

            }
        }
    }

    override fun onClick(v: View?) {
        onCategoryClickListener.onCategoryClick(adapterPosition)
    }

}