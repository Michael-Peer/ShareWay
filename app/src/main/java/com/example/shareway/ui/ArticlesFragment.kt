package com.example.shareway.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shareway.R
import com.example.shareway.adapters.ArticleListAdapter
import com.example.shareway.databinding.FragmentArticlesBinding
import com.example.shareway.listeners.ArticleItemTouchHelper
import com.example.shareway.listeners.OnArticleClickListener
import com.example.shareway.listeners.OnSwipeListener
import com.example.shareway.listeners.UICommunicationListener
import com.example.shareway.models.Article
import com.example.shareway.utils.UIComponentType
import com.example.shareway.utils.modes.FilterMode
import com.example.shareway.viewmodels.ArticlesViewModel
import com.example.shareway.viewstates.ArticlesViewState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class ArticlesFragment : Fragment(), OnArticleClickListener, OnSwipeListener {

    companion object {
        private const val TAG = "ArticlesFragment"
    }

    private lateinit var uiCommunicationListener: UICommunicationListener
    private lateinit var binding: FragmentArticlesBinding
    private lateinit var articleListRecyclerViewAdapter: ArticleListAdapter

    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var articleTouchHelper: ItemTouchHelper


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

        //TODO: Move to function
        val callback: ItemTouchHelper.Callback =
            ArticleItemTouchHelper(
                context = requireContext(),
                adapter = articleListRecyclerViewAdapter
            )

        articleTouchHelper = ItemTouchHelper(callback)
        articleTouchHelper.attachToRecyclerView(binding.articlesRecyclerView)


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
                    if (it.articles.isEmpty()) {
                        binding.noContentText.visibility = View.VISIBLE
                    } else {
                        binding.noContentText.visibility = View.GONE
                        articleListRecyclerViewAdapter.submitList(it.articles)
                        Log.d(TAG, "DATA")
                    }

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
            articleListRecyclerViewAdapter = ArticleListAdapter(this, this)
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

//    override fun onArticleClick(position: Int) {
//        Log.d(TAG, "onCategoryClick: position: $position")
//        val articleURL = articleListRecyclerViewAdapter.getCurrentURL(position)
//        articleURL?.let {
//            val action =
//                ArticlesFragmentDirections.actionArticlesFragmentToArticleDetailFragment(it)
//            findNavController().navigate(action)
//        }
//    }

    override fun onArticleClick(position: Int) {
        Log.d(TAG, "onCategoryClick: position: $position")
//        val articleURL = articleListRecyclerViewAdapter.getCurrentURL(position)
        val articlesURLList = articleListRecyclerViewAdapter.getArticlesUrlList(position)
        articlesURLList?.let {
            val action =
                ArticlesFragmentDirections.actionArticlesFragmentToArticleDetailFragment(it.toTypedArray())
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
        if (articles.isNotEmpty()) {
            val copyList = articles.toList()
            articlesViewModel.updateMultipleMarkAsRead(copyList)
        }
    }

    /**
     *
     * END OF RELATED FUNCTIONS EXPLANATION
     *
     * **/


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

    /**
     *
     * Exit selection mode
     *
     * **/
    fun onBackButtonPressed() {
        articleListRecyclerViewAdapter.clearSelection()
    }


    /**
     *
     * Called when we swipe to delete the article.
     * Will trigger undo snackbar before actual deleting
     *
     * **/
    override fun onSwipeToDelete(position: Int, itemToDelete: Article) {
        articleListRecyclerViewAdapter.getCurrentArticle(position)?.let {
            showUndoSnackbar(position, itemToDelete)
        }
    }

    /**
     *
     * After the use swipe to delete, we show him a snackbar with UNDO possibility
     * we DON'T remove the item right away.
     * We register a listener to know when the snackbar dismissed, if it dismissed without pressing the UNDO, we trigger the actual delete proccess.
     * If user press on the UNDO, we call [articleListRecyclerViewAdapter.notifyItemChanged(position)], indicate that nothing will happened and item should go back to where he was.
     *
     * **/
    private fun showUndoSnackbar(
        position: Int,
        itemToDelete: Article
    ) {
        val snackbar =
            Snackbar.make(binding.root, "Article was removed from the list", Snackbar.LENGTH_LONG)
        snackbar.setAction("UNDO") {
            uiCommunicationListener.onResponseReceived("Undo action", UIComponentType.Toast)
            articleListRecyclerViewAdapter.notifyItemChanged(position)
        }
        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                if (event == DISMISS_EVENT_TIMEOUT) {
                    articlesViewModel.deleteArticle(itemToDelete)
                }
            }
        })
        snackbar.show()
    }


    /**
     *
     * Called when we swipe to mark the article as already read.
     * first we check if the item is already mark as already read, if it does we show a toast with message(TODO: Enable reverse already read)
     * If it not marked as already read, we first create a handler and set a delay, without it the item won't get back to where he was after swiping
     * than we trigger the update proccess
     *
     * **/
    override fun onSwipeToAlreadyRead(position: Int, alreadyRead: Boolean) {

        val handle = Handler()
        if (!alreadyRead) {
            articleListRecyclerViewAdapter.getCurrentURL(position)?.let {

                articlesViewModel.updateAlreadyRead(it)

                handle.postDelayed({
                    articleListRecyclerViewAdapter.notifyItemChanged(position)
                }, 1000)
            }
        } else {
            uiCommunicationListener.onResponseReceived(
                "You already mark it as already read",
                UIComponentType.Toast
            )
            articleListRecyclerViewAdapter.notifyItemChanged(position)

        }


    }

}