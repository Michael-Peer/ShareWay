package com.example.shareway.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shareway.adapters.ArticleListAdapter
import com.example.shareway.databinding.FragmentArticlesBinding
import com.example.shareway.listeners.OnArticleClickListener
import com.example.shareway.listeners.UICommunicationListener
import com.example.shareway.viewmodels.ArticlesViewModel
import com.example.shareway.viewstates.ArticlesViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class ArticlesFragment : Fragment(), OnArticleClickListener {

    companion object {
        private const val TAG = "ArticlesFragment"
    }

    private lateinit var uiCommunicationListener: UICommunicationListener
    private lateinit var binding: FragmentArticlesBinding
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var articleListRecyclerViewAdapter: ArticleListAdapter

    private val articlesViewModel: ArticlesViewModel by viewModel()
    private val args: ArticlesFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ")
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ")
        return FragmentArticlesBinding.inflate(inflater, container, false).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ${args.domainName}")

        observeViewState()
        initRecyclerView()
        getArticles()


    }

    private fun observeViewState() {
        articlesViewModel.viewState.observe(viewLifecycleOwner, Observer {

            uiCommunicationListener.displayProgressBar(it is ArticlesViewState.Loading)
            when (it) {


                is ArticlesViewState.Error -> {
                    uiCommunicationListener.onResponseReceived(
                        uiComponentType = it.messageType,
                        message = it.errorMessage
                    )
                    Log.d(TAG, "ERROR ${it.errorMessage}")

                }

                is ArticlesViewState.ArticleList -> {
//                    uiCommunicationListener.displayProgressBar(false)
                    Log.d(TAG, "ArticlesViewState.ArticleList: ${it.articles.size}")
                    articleListRecyclerViewAdapter.submitList(it.articles)
                    Log.d(TAG, "DATA")
                }
            }
        })
    }

    private fun getArticles() {
        lifecycleScope.launch {
            articlesViewModel.getArticles(args.domainName)
        }
    }

    private fun initRecyclerView() {
        Log.d(TAG, "initRecyclerView: ")
        activity?.let {
            gridLayoutManager = GridLayoutManager(it, 3)
            articleListRecyclerViewAdapter = ArticleListAdapter(this)
            binding.articlesRecyclerView.apply {
                layoutManager = gridLayoutManager
                adapter = articleListRecyclerViewAdapter
            }

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            uiCommunicationListener = context as UICommunicationListener
        } catch (e: ClassCastException) {
            Log.d(TAG, "$context must implement UICommunicationListener")
        }
    }

    override fun onArticleClick(position: Int) {
        Log.d(TAG, "onCategoryClick: position: $position")
        val articlURL = articleListRecyclerViewAdapter.getCurrentURL(position)
        articlURL?.let {
            val action =
                ArticlesFragmentDirections.actionArticlesFragmentToArticleDetailFragment(it)
            findNavController().navigate(action)
        }
    }

    override fun onLongArticleClick(position: Int) {
        val article = articleListRecyclerViewAdapter.getCurrentArticle(position)

        article?.let { it ->
            it.alreadyRead = !(it.alreadyRead)
            articlesViewModel.insertArticle(it)

        }
    }
}