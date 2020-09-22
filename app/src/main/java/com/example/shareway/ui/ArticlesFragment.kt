package com.example.shareway.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shareway.R
import com.example.shareway.adapters.ArticleListAdapter
import com.example.shareway.databinding.FragmentArticlesBinding
import com.example.shareway.listeners.OnArticleClickListener
import com.example.shareway.listeners.UICommunicationListener
import com.example.shareway.models.Article
import com.example.shareway.utils.FilterMode
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
    private lateinit var articleListRecyclerViewAdapter: ArticleListAdapter

    private lateinit var linearLayoutManager: LinearLayoutManager

    private val articlesViewModel: ArticlesViewModel by viewModel()
    private val args: ArticlesFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ")
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

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
//            gridLayoutManager = GridLayoutManager(it, 3)
            linearLayoutManager = LinearLayoutManager(it)
            articleListRecyclerViewAdapter = ArticleListAdapter(this)
            binding.articlesRecyclerView.apply {
//                layoutManager = gridLayoutManager
                layoutManager = linearLayoutManager

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
        val articleURL = articleListRecyclerViewAdapter.getCurrentURL(position)
        articleURL?.let {
            val action =
                ArticlesFragmentDirections.actionArticlesFragmentToArticleDetailFragment(it)
            findNavController().navigate(action)
        }
    }

    override fun onLongArticleClick(position: Int) {
        val article = articleListRecyclerViewAdapter.getCurrentArticle(position)


        article?.let { it ->
//            val alreadyRead = !(it.alreadyRead)
//            it.alreadyRead = !(it.alreadyRead)
//            articlesViewModel.insertArticle(it)
            articlesViewModel.updateAlreadyRead(it.url)
        }
    }

    override fun onEnterMultiSelectionMode(articleListAdapter: ArticleListAdapter) {
        activity?.startActionMode(articleListAdapter)
    }

    /**
     *
     * Here was an interesting case.
     * In the adapter I'm calling [onArticleClickListener.onDeleteMultipleArticles(selectedItems)] after I click on delete in selection mode, which lead me to this function.
     * Here I just send the list to the repository through the view model, and in the repo I jest launch a coroutine scope in order to make DB call.
     * THE PROBLEM WAS When I entered into the coroutine scope, suddenly the list become empty.
     * The cause to that was that after I call [onArticleClickListener.onDeleteMultipleArticles(selectedItems)] I'm calling also [mode?.finish()], which reset my list.
     *
     * When I launched the coroutine, the function returned immediately(because the async nature of coroutines of course), and [mode?.finish()] and my list was reset.
     * Which lead to empty list because before the coroutine handled the list, [mode?.finish()] called.
     *
     * The fix was relatively easy - make a copy of the list
     *
     *
     * **/
    override fun onDeleteMultipleArticles(articles: List<Article>) {
        if (articles.isNotEmpty()) {
            val copyList = articles.toList()
            articlesViewModel.deleteArticles(copyList)
        }
    }

    override fun onMarkAsReadMultipleArticles(articles: List<Article>) {

        articlesViewModel.updateMultipleMarkAsRead(articles)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.article_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.filter_all -> {
                item.isChecked = true
                lifecycleScope.launch {
                    articlesViewModel.getArticles(args.domainName)
                }
                return true
            }
            R.id.filter_already_read -> {
                item.isChecked = true
                lifecycleScope.launch {
                    articlesViewModel.getArticles(args.domainName, FilterMode.ALREADY_READ)
                }
                return true
            }
            R.id.filter_no_read -> {
                item.isChecked = true
                lifecycleScope.launch {
                    articlesViewModel.getArticles(args.domainName, FilterMode.NOT_READ)
                }
                return true
            }
            R.id.order_by_name -> {
                item.isChecked = true
                return true
            }
            R.id.order_by_date -> {
                item.isChecked = true
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onBackButtonPressed() {
        articleListRecyclerViewAdapter.clearSelection()
    }

}