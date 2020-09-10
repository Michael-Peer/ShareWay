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
        binding.textView.setOnClickListener(this)
        binding.imageView.setOnClickListener(this)
    }

    fun bind(categoryItem: Category?) {
        var hasNewName = false
        categoryItem?.let {
            if (it.newCategoryName != "") {
                hasNewName = true
            }
            binding.apply {
                textView.text =
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

            binding.textView -> {
                Log.d(TAG, "onClick:  binding.textView")
                replaceToEditMode()
            }

            binding.imageView -> {
                saveEdit()
            }
        }
    }


    private fun replaceToEditMode() {
        val currentText = binding.textView.text
        if (currentText.isNotEmpty()) {
            binding.textView.visibility = View.GONE
            binding.editTextTextPersonName.setText(currentText)
            binding.editTextTextPersonName.visibility = View.VISIBLE
            binding.imageView.visibility = View.VISIBLE
        }
    }

    private fun saveEdit() {
        if (binding.textView.text != binding.editTextTextPersonName.text) {
            binding.editTextTextPersonName.visibility = View.GONE
            binding.imageView.visibility = View.GONE
            binding.textView.text = binding.editTextTextPersonName.text
            binding.textView.visibility = View.VISIBLE
            onCategoryClickListener.onCheckIconClick(
                binding.textView.text.toString(),
                adapterPosition
            )

        }
    }

    fun triggerListner() {
        onClick(itemView)
    }

}