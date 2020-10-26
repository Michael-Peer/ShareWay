package com.example.shareway.adapters

import android.annotation.SuppressLint
import android.util.Log
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
import com.example.shareway.viewholders.CategoryListViewHolder
import kotlinx.android.synthetic.main.category_list_item.view.*
import java.util.*
import kotlin.collections.ArrayList

class CategoryListAdapter(
    private val onCategoryClickListener: OnCategoryClickListener,
    private val startDragListener: OnStartDragListener
) :
    ListAdapter<Category, CategoryListViewHolder>(DIFF_CALLBACK), RowMovesListener {

    private var mutableCopyList: List<Category> = ArrayList()

    private var isEditMode = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val view =
//            LayoutInflater.from(parent.context).inflate(R.layout.category_list_item, parent, false)
            CategoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryListViewHolder(view, onCategoryClickListener)
    }

    /**
     *
     * The problem here was that onCLick event and onDrag event cannot work together.
     * So initially I checked the MotionEvent(s) to see which event will trigger when.
     * And I saw that when I click on item I get ACTION_UP & ACTION_DOWN
     * And when I drag the item I get ACTION_DOWN & ACTION_MOVE.
     * I thought to call the click listener in ACTION_UP cause it'll be fire only on click, and call the start drag in ACTION_MOVE cause it'll be triggered only when drag.
     * It DOES WORK.
     * BUT the problem was the drag detection. around half the times the ON_MOVE didn't detect the drag events.
     * I read the docs of OnTouchListener and i saw there something interesting - if I return true the event is consumed, if I return false it doesn't.
     * What this is mean is that if I return false, it move forward to other listeners.
     * It worked but I was surprised that why when I click it call onDrag and OnClick(as expected), but if I drag it doesn't call the OnClick.
     * If I return false it should move forward to the next listeners, right?
     * So yes. it should. but because I'm dragging the item, it detect OnLongClick event and not OnClick.
     * This is how I solved the problem
     *
     * If I needed to detect LongClick? I probably would check if the item is moved or not(relative position to initial position)
     *
     * **/
    @SuppressLint("ClickableViewAccessibility", "LongLogTag")
    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        val categoryItem = getItem(position)
        holder.bind(categoryItem)
        if (!isEditMode) {
            holder.itemView.categoryCardView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    this.startDragListener.onStartDrag(holder)
                }
                return@setOnTouchListener false
            }
        }


//        holder.itemView.container.setOnTouchListener { v, event ->
//            when (event.action) {
//                MotionEvent.ACTION_UP -> {
//                    holder.triggerListner()
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    this.startDragListener.onStartDrag(holder)
//                }
//            }
//            return@setOnTouchListener true
//        }
    }

    fun getCurrentDomainName(position: Int): String? {
        return if (currentList.isNotEmpty()) {
            val item = getItem(position)
            item.originalCategoryName
        } else {
            null
        }
    }

    fun getCurrentCategory(position: Int): Category? {
        return if (!currentList.isNullOrEmpty()) {
            getItem(position)
        } else {
            null
        }
    }

    fun modifyList(list: List<Category>) {
        mutableCopyList = list
        submitList(list)
    }

    fun filter(query: String?) {
        val filteredList = ArrayList<Category>()

        if (!query.isNullOrEmpty()) {
            filteredList.addAll(mutableCopyList.filter { category: Category ->
                category.newCategoryName.toLowerCase()
                    .contains(query.toString().toLowerCase().trim())
            })
        } else {
            filteredList.addAll(mutableCopyList)
        }

        //TODO: disable drag while filter || save to data base when drag not when pause
        submitList(filteredList)
    }


    /**
     *
     * Initially, I wrote  Collections.swap(currentList, i, i + 1) and I got a crash.
     * the problem was the "currentList" that came from diff util is a "regular" list and not mutable list.
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

    @SuppressLint("LongLogTag")
    override fun onRowSelected(itemViewHolder: CategoryListViewHolder) {
        Log.d(TAG, "onRowSelected: ")
    }

    @SuppressLint("LongLogTag")
    override fun onRowClear(itemViewHolder: CategoryListViewHolder) {
        Log.d(TAG, "onRowClear: ")
    }

    fun changeEditMode(editMode: Boolean) {
        isEditMode = editMode
    }


    companion object {
        private const val TAG = "CategoryL return@setOnTouchListener truestAdapter"

        val DIFF_CALLBACK: DiffUtil.ItemCallback<Category> =
            object : DiffUtil.ItemCallback<Category>() {
                override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
                    return oldItem.originalCategoryName == newItem.originalCategoryName
                }

                override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
                    return oldItem == newItem
                }
            }
    }


}