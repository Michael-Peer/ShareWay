package com.example.shareway.viewholders

import android.util.Log
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

    companion object {
        private const val TAG = "CategoryListViewHolder"
    }

    init {
        binding.root.setOnClickListener(this)
        binding.categoryNameText.setOnClickListener(this)
        binding.acceptEditIcon.setOnClickListener(this)
    }

    fun bind(categoryItem: Category?) {

//        if (categoryItem != null) {
//            Log.d(TAG, "bind: Number of articles ${categoryItem.numberOfArticles}")
//            binding.tempArticleNum.text = categoryItem.numberOfArticles.toString()
//        }
        var hasNewName = false
        categoryItem?.let {
//            if (it.newCategoryName != "") {
            if (it.newCategoryName != it.originalCategoryName) {
                hasNewName = true
            }
            binding.apply {
                categoryNameText.text =
                    if (!hasNewName) categoryItem.originalCategoryName else categoryItem.newCategoryName

            }
        }
    }


    override fun onClick(v: View?) {
        when (v) {
            binding.root -> {
                Log.d(TAG, "onClick:  binding.root ")
                onCategoryClickListener.onCategoryClick(adapterPosition)
            }

            binding.categoryNameText -> {
                Log.d(TAG, "onClick:  binding.textView")
                replaceToEditMode()
            }

            binding.acceptEditIcon -> {
                saveEdit()
            }
        }
    }


    private fun replaceToEditMode() {
        val currentText = binding.categoryNameText.text
        if (currentText.isNotEmpty()) {
            binding.categoryNameText.visibility = View.GONE
            binding.categoryNameEditText.setText(currentText)
            binding.categoryNameEditText.visibility = View.VISIBLE
            binding.acceptEditIcon.visibility = View.VISIBLE
        }
    }

    private fun saveEdit() {
        if (binding.categoryNameText.text != binding.categoryNameEditText.text && binding.categoryNameEditText.text.isNotEmpty()) {
            binding.categoryNameEditText.visibility = View.GONE
            binding.acceptEditIcon.visibility = View.GONE
            binding.categoryNameText.text = binding.categoryNameEditText.text
            binding.categoryNameText.visibility = View.VISIBLE
            onCategoryClickListener.onCheckIconClick(
                binding.categoryNameText.text.toString(),
                adapterPosition
            )

        }
    }

    fun triggerListner() {
        onClick(itemView)
    }

}