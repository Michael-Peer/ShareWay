package com.example.shareway.viewholders

import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
            Glide.with(itemView)
//                .load("http://www.google.com/s2/favicons?sz=72&domain=${categoryItem.baseUrl}")
//                .load("${categoryItem.baseUrl}/apple-touch-icon.png")
                .load(categoryItem.faviconUrl)
//                .load("http://logo.clearbit.com/${categoryItem.baseUrl}")


                .centerCrop()
                .into(binding.favicon)
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
                binding.categoryNameEditText.setOnEditorActionListener { v, actionId, event ->
                    Log.d(TAG, "onEditorAction: $actionId")
                    Log.d(TAG, "onEditorAction: $event")
                    saveEdit()
                    false
                }
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
            requestFocusAndOpenKeyboard()
            binding.categoryNameEditText.requestFocus()
            requestFocusAndOpenKeyboard()
        }
    }

    /**
     *
     * focus on the edit text and open the keyboard
     *
     * **/
    private fun requestFocusAndOpenKeyboard() {
        binding.categoryNameEditText.requestFocus()
        val imm = itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
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