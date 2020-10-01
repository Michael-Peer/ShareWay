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

//    fun bind(articleItem: Article?) {
//        Log.d(TAG, "bind: $articleItem ")
//        articleItem?.let {
//            Log.d(TAG, "bind: Date Added: ${it.dateAdded}")
//            binding.apply {
//                textView.text = articleItem.url
//                Log.d(TAG, "bind: ALREADY READ ${it.alreadyRead}")
//                if (it.alreadyRead) binding.alreadyReadIcon.visibility =
//                    View.VISIBLE else binding.alreadyReadIcon.visibility = View.GONE
//            }
//        }
//
//    }

    fun bind(articleItem: Article?) {

        Log.d(TAG, "bind: $articleItem ")
        articleItem?.let {
            Log.d(TAG, "bind: Date Added: ${it.dateAdded}")
            binding.apply {
                textView.text = articleItem.url
                Log.d(TAG, "bind: ALREADY READ ${it.alreadyRead}")
                if (it.alreadyRead) binding.alreadyReadIcon.visibility =
                    View.VISIBLE else binding.alreadyReadIcon.visibility = View.GONE
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
        if (binding.alreadyReadIcon.isVisible) {
            Log.d(TAG, "bind: ALREADY READ VISIBILITY = VISIBLE! NOW IT ISN'T")
            binding.alreadyReadIcon.visibility = View.GONE
        } else {
            Log.d(TAG, "bind: ALREADY READ VISIBILITY = NOT(!!!) VISIBLE! NOW IT IS")
            binding.alreadyReadIcon.visibility = View.VISIBLE
        }
        //true if the callback consumed the long click, false otherwise.
        onArticleClickListener.onLongArticleClick(adapterPosition)
        return true
    }

    fun triggerOnClickListener() {
        onClick(itemView)
    }


}