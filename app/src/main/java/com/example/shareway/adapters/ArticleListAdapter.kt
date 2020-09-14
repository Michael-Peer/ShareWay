package com.example.shareway.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.shareway.databinding.ArticleListItemBinding
import com.example.shareway.listeners.OnArticleClickListener
import com.example.shareway.models.Article
import com.example.shareway.viewholders.ArticleListViewHolder

class ArticleListAdapter(private val onArticleClickListener: OnArticleClickListener) :
    ListAdapter<Article, ArticleListViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleListViewHolder {
        val view =
//            LayoutInflater.from(parent.context).inflate(R.layout.category_list_item, parent, false)
            ArticleListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        Log.d(TAG, "onCreateViewHolder: ")
        return ArticleListViewHolder(view, onArticleClickListener)
    }

    override fun onBindViewHolder(holder: ArticleListViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: ")
        val articleItem = getItem(position)
        holder.bind(articleItem)
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
}