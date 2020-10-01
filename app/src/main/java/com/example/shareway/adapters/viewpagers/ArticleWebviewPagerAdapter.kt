package com.example.shareway.adapters.viewpagers

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.recyclerview.widget.RecyclerView
import com.example.shareway.R
import kotlinx.android.synthetic.main.fragment_article_detail.view.*

class ArticleWebviewPagerAdapter(
    val articlesUrl: List<String>
) : RecyclerView.Adapter<ArticleWebviewPagerAdapter.ArticleWebviewPagerViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticleWebviewPagerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_article_detail, parent, false)
        return ArticleWebviewPagerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return articlesUrl.size
    }

    override fun onBindViewHolder(holder: ArticleWebviewPagerViewHolder, position: Int) {
        val articleUrl = articlesUrl[position]
        holder.bind(articleUrl)
    }

    class ArticleWebviewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetJavaScriptEnabled")
        fun bind(articleUrl: String) {
            itemView.webView.apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadUrl(articleUrl)
            }
        }

    }

}