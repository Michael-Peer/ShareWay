package com.example.shareway.viewholders

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.shareway.R
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

    private var originalName: String? = null
    var isClickable: Boolean = true

    init {
        binding.root.setOnClickListener(this)
        binding.categoryNameText.setOnClickListener(this)
        binding.acceptEditIcon.setOnClickListener(this)
        binding.resetIcon.setOnClickListener(this)
        binding.popupMenu.setOnClickListener(this)
    }

    fun bind(categoryItem: Category) {

        originalName = categoryItem.originalCategoryName
        isClickable = categoryItem.isClickable


        var hasNewName = false
        //TODO: change check
        categoryItem?.let {


            binding.favicon.load(categoryItem.faviconUrl) {
                crossfade(true)
                transformations(CircleCropTransformation())
            }

//            if (it.newCategoryName != "") {
//            Glide.with(itemView)
//                .load("http://www.google.com/s2/favicons?sz=72&domain=${categoryItem.baseUrl}")
//                .load("${categoryItem.baseUrl}/apple-touch-icon.png")
//                .load(categoryItem.faviconUrl)
//                .load("http://logo.clearbit.com/${categoryItem.baseUrl}")
//                .centerCrop()
//                .listener(object : RequestListener<Drawable> {
//                    override fun onLoadFailed(
//                        e: GlideException?,
//                        model: Any?,
//                        target: Target<Drawable>?,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun onResourceReady(
//                        resource: Drawable?,
//                        model: Any?,
//                        target: Target<Drawable>?,
//                        dataSource: DataSource?,
//                        isFirstResource: Boolean
//                    ): Boolean {
//
//                        return true
//                    }
//
//                })
//                .into(binding.favicon)

            if (it.newCategoryName != it.originalCategoryName) {
                hasNewName = true
            }
            binding.apply {


                categoryNameText.text =
                    if (!hasNewName) categoryItem.originalCategoryName else categoryItem.newCategoryName

            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onClick(v: View?) {
        Log.d(TAG, "onClick: is clickable $isClickable")
        if (isClickable) {
            when (v) {
                binding.root -> {
////                Log.d(TAG, "onClick:  binding.root ${binding.favicon.isVisible} ")
////                Log.d(TAG, "onClick: drawable ${binding.favicon.drawable}")
//                val bitmap = (binding.favicon.drawable as BitmapDrawable).bitmap
//                val boundsTOP = binding.favicon.drawable.bounds.top
//                val boundsRIGHT = binding.favicon.drawable.bounds.right
//                Log.d(TAG, "onClick: bounds: top: ${bitmap.height} right: ${bitmap.width}")
//                val pixel = bitmap.getColor(bitmap.width - 5, bitmap.height - 5)
//                Log.d(TAG, "onClick: pixel: $pixel")
////                binding.backgroindc.background = pixel.toDrawable()
//                val drawable = binding.favicon.background as ColorDrawable
//                binding.backgroindc.background = drawable.color.toDrawable()
//                val pal = Palette.Builder(bitmap)
//                val palette = pal.generate { palette ->
////                    binding.backgroindc.background = palette?.mutedSwatch?.rgb.toDrawable()
//                    Log.d(TAG, "onClick: ${palette?.vibrantSwatch}")
//                    Log.d(TAG, "onClick: ${palette?.darkVibrantSwatch}")
//                    Log.d(TAG, "onClick: ${palette?.lightVibrantSwatch}")
//                    Log.d(TAG, "onClick: ${palette?.mutedSwatch}")
//                    Log.d(TAG, "onClick: ${palette?.darkMutedSwatch}")
//                    Log.d(TAG, "onClick: ${palette?.lightMutedSwatch}")
//
//                }
                onCategoryClickListener.onCategoryClick(adapterPosition)
                }

                binding.categoryNameText -> {

                    Log.d(TAG, "onClick:  binding.textView")
                    replaceToEditMode()
                    //keyboard V icon
                    binding.categoryNameEditText.setOnEditorActionListener { v, actionId, event ->
                        Log.d(TAG, "onEditorAction: $actionId")
                        Log.d(TAG, "onEditorAction: $event")
                        saveEdit()

                        true
                    }
                }


                binding.acceptEditIcon -> {
                    saveEdit()
                }

                binding.resetIcon -> {
                    Log.d(TAG, "onClick: reset icon")
                    resetToOriginalName()
                }
                binding.popupMenu -> showPopupMenu(v)

            }

        }

    }

    private fun showPopupMenu(v: View) {
        val popupMenu = PopupMenu(v.context, v)
        popupMenu.inflate(R.menu.category_list_item_menu)
        popupMenu.setOnMenuItemClickListener {

            when (it.itemId) {
                R.id.edit_mode -> {
                    Log.d(TAG, "onClick:  binding.textView")
                    replaceToEditMode()
                    //keyboard V icon
                    binding.categoryNameEditText.setOnEditorActionListener { v, actionId, event ->
                        Log.d(TAG, "onEditorAction: $actionId")
                        Log.d(TAG, "onEditorAction: $event")
                        saveEdit()

                        true
                    }
                    return@setOnMenuItemClickListener true
                }

                R.id.mark_all_as_read -> {
//                    Log.d(ArticleListViewHolder.TAG, "showPopupMenu: set reminder clicked")
//                    onSetReminderClicked()
                    onCategoryClickListener.onMarkAsRead(adapterPosition)
                    return@setOnMenuItemClickListener true
                }

                R.id.delete_all -> {
                    onCategoryClickListener.onDelete(adapterPosition)
                    return@setOnMenuItemClickListener true

                }

                else -> return@setOnMenuItemClickListener true

            }
        }
        popupMenu.show()
    }

    private fun resetToOriginalName() {
        Log.d(TAG, "resetToOriginalName: reset icon")
        binding.categoryNameEditText.visibility = View.GONE
        binding.acceptEditIcon.visibility = View.GONE
        binding.resetIcon.visibility = View.GONE
        binding.categoryNameText.text = originalName
        binding.categoryNameText.visibility = View.VISIBLE

        onCategoryClickListener.onResetIconClick(
            adapterPosition
        )
        val imm =
            itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        onCategoryClickListener.onExitEditMode(adapterPosition)

    }


    private fun replaceToEditMode() {
        onCategoryClickListener.onEnterEditMode(adapterPosition)
        val currentText = binding.categoryNameText.text
        if (currentText.isNotEmpty()) {
            binding.categoryNameText.visibility = View.GONE
            binding.categoryNameEditText.setText(currentText)
            binding.categoryNameEditText.visibility = View.VISIBLE
            binding.acceptEditIcon.visibility = View.VISIBLE
            binding.resetIcon.visibility = View.VISIBLE
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
        val imm =
            itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun saveEdit() {
        if (binding.categoryNameText.text != binding.categoryNameEditText.text && binding.categoryNameEditText.text.isNotEmpty()) {
            binding.categoryNameEditText.visibility = View.GONE
            binding.acceptEditIcon.visibility = View.GONE
            binding.resetIcon.visibility = View.GONE
            binding.categoryNameText.text = binding.categoryNameEditText.text
            binding.categoryNameText.visibility = View.VISIBLE
            onCategoryClickListener.onCheckIconClick(
                binding.categoryNameText.text.toString(),
                adapterPosition
            )
            val imm =
                itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
//            binding.categoryNameText.setOnClickListener(this)
            onCategoryClickListener.onExitEditMode(adapterPosition)
        }


    }

    fun triggerListner() {
        onClick(itemView)
    }

}