package com.example.shareway.adapters

import android.util.Log
import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.shareway.R
import com.example.shareway.databinding.ArticleListItemBinding
import com.example.shareway.listeners.OnArticleClickListener
import com.example.shareway.listeners.OnSwipeListener
import com.example.shareway.models.Article
import com.example.shareway.viewholders.ArticleListViewHolder
import kotlinx.android.synthetic.main.article_list_item.view.*

class ArticleListAdapter(
    private val onArticleClickListener: OnArticleClickListener,
    private val onSwipeListener: OnSwipeListener
) :
    ListAdapter<Article, ArticleListViewHolder>(DIFF_CALLBACK), ActionMode.Callback {

    // true if the user in selection mode, false otherwise
    private var multiSelectionMode = false

    // Keeps track of all the selected articles
    private val selectedItems = arrayListOf<Article>()

    companion object {
        private const val TAG = "ArticleListAdapter"

        val DIFF_CALLBACK: DiffUtil.ItemCallback<Article> =
            object : DiffUtil.ItemCallback<Article>() {
                override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                    return oldItem.url == newItem.url
                }

                override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                    return oldItem == newItem
                }

            }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleListViewHolder {
        val view =
//            LayoutInflater.from(parent.context).inflate(R.layout.category_list_item, parent, false)
            ArticleListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        Log.d(TAG, "onCreateViewHolder: ")
        return ArticleListViewHolder(view, onArticleClickListener)
    }

//    override fun onBindViewHolder(holder: ArticleListViewHolder, position: Int) {
//        Log.d(TAG, "onBindViewHolder: ")
//        val articleItem = getItem(position)
//        holder.bind(articleItem)
//    }

    override fun onBindViewHolder(holder: ArticleListViewHolder, position: Int) {
        val articleItem = getItem(position)

        if (selectedItems.contains(articleItem)) {
            holder.itemView.constraint_container.alpha = 0.3f
        } else {
            holder.itemView.constraint_container.alpha = 1.0f
        }

        holder.itemView.constraint_container.setOnLongClickListener {
            if (!multiSelectionMode) {
                multiSelectionMode = true
                // As soon as the user starts multi-select process, show the contextual menu
                onArticleClickListener.onEnterMultiSelectionMode(this) //pass the interface

                Log.d(
                    TAG,
                    "onBindViewHolder: setOnLongClickListener SELECTION MODEL = $multiSelectionMode"
                )
                selectItems(holder, articleItem)
            }
            true
        }

        holder.itemView.constraint_container.setOnClickListener {

//        if in selection mode - add item. if not - go to article detail page

            if (multiSelectionMode) {
                selectItems(holder, articleItem)
            } else {
                holder.triggerOnClickListener()
            }
        }
        holder.bind(articleItem)
    }

    private fun selectItems(holder: ArticleListViewHolder, articleItem: Article?) {
        if (selectedItems.contains(articleItem)) {
            Log.d(TAG, "selectItems: inside if")
            selectedItems.remove(articleItem) //TODO: Exit ActionMode when nothing left?
            holder.itemView.constraint_container.alpha = 1.0f
        } else {
            Log.d(TAG, "selectItems: inside else")
            selectedItems.add(articleItem!!)
            holder.itemView.constraint_container.alpha = 0.3f
        }
    }

    fun getCurrentURL(position: Int): String? {
        Log.d(TAG, "getCurrentDomainName: ")
        return if (currentList.isNotEmpty()) {
            val item = getItem(position)
            item.url
        } else {
            null
        }
    }

    fun getCurrentArticle(position: Int): Article? {
        return if (!currentList.isNullOrEmpty()) {
            getItem(position)
        } else {
            null
        }
    }


    /**
     *
     * Those override methods come from ActionMode.Callback
     * Those methods help us inflate the "Multi Selection App Bar" when we enter into the selection mode.
     * We enter in the multi selection mode in the onBindViewHolder method by calling -   onArticleClickListener.onEnterMultiSelectionMode(this)
     * this refer to ActionMode.Callback we're implementing here
     *
     *
     * **/

    // Called when a menu item was clicked
    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {

        item?.let {
            when (it.itemId) {
                R.id.action_delete -> {
                    // Delete button is clicked, handle the deletion and finish the multi select process
//            Toast.makeText(, "Selected Articles Delete", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "onActionItemClicked: Selected Articles Delete ")
                    onArticleClickListener.onDeleteMultipleArticles(selectedItems)
                }

                R.id.action_mark_as_read -> {
                    onArticleClickListener.onMarkAsReadMultipleArticles(selectedItems)
                }
            }
            mode?.finish()
        }

        return true
    }


    // Called when the menu is created i.e. when the user starts multi-select mode (inflate your menu xml here)
    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.let {
            val inflater = it.menuInflater
            inflater.inflate(R.menu.mutli_selection_menu, menu)
        }
        return true
    }


    // Called to refresh an action mode's action menu (we won't be using this here)
    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    // Called when the Context ActionBar disappears i.e. when the user leaves multi-select mode
    override fun onDestroyActionMode(mode: ActionMode?) {
        // finished multi selection
        multiSelectionMode = false
        selectedItems.clear()
        notifyDataSetChanged()
    }


    /**
     *
     * clear when we exit multi selection mode
     *
     * **/

    fun clearSelection() {
        selectedItems.clear()
        multiSelectionMode = false
    }


    fun isMultiSelectionActive(): Boolean {
        return multiSelectionMode
    }

    fun onSwipedDelete(adapterPosition: Int) {
        Log.d(TAG, "onArticleSwiped: SWIPED AT POSITION $adapterPosition LEFT LEFT LEFT")
        val itemToDelete = getItem(adapterPosition)
        onSwipeListener.onSwipeToDelete(adapterPosition, itemToDelete)
    }


    fun onSwipedAlreadyRead(adapterPosition: Int) {
        Log.d(TAG, "onArticleSwiped: SWIPED AT POSITION $adapterPosition RIGHT RIGHT RIGHT")
//        onSwipeListener.onSwipeToAlreadyRead(adapterPosition)
        if (!getItem(adapterPosition).alreadyRead) {
            /**
             *
             * Not mark as read yet
             *
             * **/
            onSwipeListener.onSwipeToAlreadyRead(adapterPosition, false)
        } else {
            /**
             *
             *  mark as read
             *
             * **/
            onSwipeListener.onSwipeToAlreadyRead(adapterPosition, true)
        }
//        notifyItemChanged(adapterPosition)
    }

    fun getArticlesUrlList(position: Int): List<String> {
        val currentList = currentList
        val urlList = arrayListOf<String>()
        for (article in currentList) {
            urlList.add(article.url)
        }

        /**
         *
         * Just for test.
         * TODO: improve preformence
         *
         *
         * **/
        urlList.remove(getItem(position).url)
        urlList.add(getItem(position).url)
        return urlList

    }


}