package com.example.shareway.viewholders

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.shareway.databinding.ArticleListItemBinding
import com.example.shareway.listeners.OnArticleClickListener
import com.example.shareway.models.Article

class ArticleListViewHolder(
    private val binding: ArticleListItemBinding,
    private val onArticleClickListener: OnArticleClickListener
) :
    RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    companion object{
        private const val TAG = "ArticleListViewHolder"
    }

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(articleItem: Article?) {
        Log.d(TAG, "bind: $articleItem ")
        articleItem?.let {
            binding.apply {
                textView.text = articleItem.url
            }
        }
    }

    override fun onClick(v: View?) {
        onArticleClickListener.onArticleClick(adapterPosition)
    }

}