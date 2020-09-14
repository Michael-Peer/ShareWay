package com.example.shareway.viewholders

import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.shareway.databinding.ArticleListItemBinding
import com.example.shareway.listeners.OnArticleClickListener
import com.example.shareway.models.Article

class ArticleListViewHolder(
    private val binding: ArticleListItemBinding,
    private val onArticleClickListener: OnArticleClickListener
) :
    RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

    companion object {
        private const val TAG = "ArticleListViewHolder"
    }

    init {
        itemView.setOnClickListener(this)
//        itemView.setOnClickListener(object : DoubleClickListener() {
//            override fun onDoubleClick(v: View) {
//                Log.d(TAG, "onDoubleClick: DOUBLE CLICKED")
//            }
//
//        })
        itemView.setOnLongClickListener(this)
    }

    fun bind(articleItem: Article?) {
        Log.d(TAG, "bind: $articleItem ")
        articleItem?.let {
            binding.apply {
                textView.text = articleItem.url
                if (it.alreadyRead) binding.alreadyReadIcon.visibility = View.VISIBLE
            }
        }

    }

    override fun onClick(v: View?) {
        Log.d(TAG, "onClick: clicked")
        onArticleClickListener.onArticleClick(adapterPosition)
    }

    /**
     *
     * onLongClick - check visibility and set the opposite.
     * Insert isAlreadyRead status to db
     *
     * **/
    override fun onLongClick(v: View?): Boolean {
        Log.d(TAG, "onLongClick: clicked")
        //check if true or false
        if (binding.alreadyReadIcon.isVisible) binding.alreadyReadIcon.visibility =
            View.GONE else binding.alreadyReadIcon.visibility = View.VISIBLE
        //true if the callback consumed the long click, false otherwise.
        onArticleClickListener.onLongArticleClick(adapterPosition)
        return true
    }


}