package com.example.shareway.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
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
import com.example.shareway.listeners.*
import com.example.shareway.models.Article
import com.example.shareway.receivers.RemainderReceiver
import com.example.shareway.utils.UIComponentType
import com.example.shareway.utils.managers.DateTimeManager
import com.example.shareway.utils.modes.FilterMode
import com.example.shareway.viewmodels.ArticlesViewModel
import com.example.shareway.viewstates.ArticlesViewState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.Instant
import java.util.*

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


    /**
     *
     * TODO: Move vars to viewModel in order to save the state
     * TODO: Move vars to viewModel in order to save the state
     * TODO: Move vars to viewModel in order to save the state
     *
     * Here we need second [FilterMode] var, and here is why:
     * In order to set the initial selection mode, I override the [onPrepareOptionsMenu] method, checking the [filterMode] state and set the checked marker to the correct item.
     * The "problem" is that [onPrepareOptionsMenu] called whenever we click on the three dots menu.
     * If I'll set the [when] statement to [args.filterMode], every time that we change the filter state, we DO save all correctly, but visually, [onPrepareOptionsMenu] called again with the same [args.filterMode] so we see the old state.
     * Here I init the [currentFilterMode] in [onCreate], and set to it a new var whenever we change filter state
     *
     * **/
    private lateinit var currentFilterMode: FilterMode


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ")
        super.onCreate(savedInstanceState)
        currentFilterMode = args.filterMode
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
        articlesViewModel.states.observe(viewLifecycleOwner, Observer {

            uiCommunicationListener.displayProgressBar(it is ArticlesViewState.Loading)
            when (it) {
                is ArticlesViewState.Error -> {
//                    uiCommunicationListener.onResponseReceived(
//                        uiComponentType = it.messageType,
//                        message = it.errorMessage
//                    )
                    uiCommunicationListener.onResponseReceived(
                        response = Response(
                            uiComponentType = it.messageType,
                            message = it.errorMessage
                        )
                    )
                    Log.d(TAG, "ERROR ${it.errorMessage}")

                }

                is ArticlesViewState.ArticleList -> {
                    Log.d(TAG, "observeViewState: is list empty ${it.articles.isEmpty()}")
//                    uiCommunicationListener.displayProgressBar(false)
                    Log.d(TAG, "ArticlesViewState.ArticleList: ${it.articles.size}")

                    /**
                     *
                     * Determine if to show "empty list" text.
                     * Either way I need to submit to list cause it need to be updated either to 0 or to it.article.size
                     *
                     * **/
                    if (it.articles.isEmpty()) {
                        Log.d(TAG, "observeViewState: EMPTY")
                        binding.noContentText.visibility = View.VISIBLE
                        binding.imageView.visibility = View.VISIBLE
                    } else {
                        binding.noContentText.visibility = View.GONE
                        binding.imageView.visibility = View.GONE

                        Log.d(TAG, "DATA")
                    }
                    articleListRecyclerViewAdapter.submitList(it.articles)
                }
            }
        })
    }

//    private fun observeViewState() {
//        articlesViewModel.viewState.observe(viewLifecycleOwner, Observer {
//
//            uiCommunicationListener.displayProgressBar(it is ArticlesViewState.Loading)
//            when (it) {
//                is ArticlesViewState.Error -> {
////                    uiCommunicationListener.onResponseReceived(
////                        uiComponentType = it.messageType,
////                        message = it.errorMessage
////                    )
//                    uiCommunicationListener.onResponseReceived(
//                        response = Response(
//                            uiComponentType = it.messageType,
//                            message = it.errorMessage
//                        )
//                    )
//                    Log.d(TAG, "ERROR ${it.errorMessage}")
//
//                }
//
//                is ArticlesViewState.ArticleList -> {
//                    Log.d(TAG, "observeViewState: is list empty ${it.articles.isEmpty()}")
////                    uiCommunicationListener.displayProgressBar(false)
//                    Log.d(TAG, "ArticlesViewState.ArticleList: ${it.articles.size}")
//
//                    /**
//                     *
//                     * Determine if to show "empty list" text.
//                     * Either way I need to submit to list cause it need to be updated either to 0 or to it.article.size
//                     *
//                     * **/
//                    if (it.articles.isEmpty()) {
//                        Log.d(TAG, "observeViewState: EMPTY")
//                        binding.noContentText.visibility = View.VISIBLE
//                        binding.imageView.visibility = View.VISIBLE
//                    } else {
//                        binding.noContentText.visibility = View.GONE
//                        binding.imageView.visibility = View.GONE
//
//                        Log.d(TAG, "DATA")
//                    }
//                    articleListRecyclerViewAdapter.submitList(it.articles)
//                }
//            }
//        })
//    }

    private fun getArticles() {
//        val sharedPref =
//            activity?.getSharedPreferences(
//                getString(R.string.filter_mode_prefs),
//                Context.MODE_PRIVATE
//            )

//        val filterMode = args.filterMode
        val filterMode = currentFilterMode

//        sharedPref?.let {
//            filterMode = sharedPref.getInt(getString(R.string.filter_mode_value), 0)
//        }
        Log.d(TAG, "getArticles: $filterMode")
//        val filterType = when (filterMode) {
//            F -> {
//                FilterMode.ALREADY_READ
//            }
//            2 -> {
//                FilterMode.NOT_READ
//            }
//            else -> {
//                FilterMode.ALL
//            }
//        }

//        Log.d(TAG, "getArticles: filter type = $filterType filterMode = $filterMode")
        lifecycleScope.launch {
//            articlesViewModel.getArticles(args.domainName, filterMode)
            articlesViewModel.getAll(filterMode, args.domainName)

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
        val articleURL = articleListRecyclerViewAdapter.getCurrentURL(position)
//        val articlesURLList = articleListRecyclerViewAdapter.getArticlesUrlList(position)
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
        if (articles.isNotEmpty()) {
            val copyList = articles.toList()
            articlesViewModel.updateMultipleMarkAsRead(copyList)
        }
    }


    override fun onReminderSet(
        position: Int,
        reminder: Instant,
        hour: Int,
        minute: Int,
        day: Int
    ) {
        val c = Calendar.getInstance()
        c.set(Calendar.DAY_OF_MONTH, day)
        c.set(Calendar.HOUR_OF_DAY, hour)
        c.set(Calendar.MINUTE, minute)
        c.set(Calendar.SECOND, 0)

        Log.d(TAG, "onReminderSet Calendar: $c")


        /**
         *
         * Great explanation why registering the broadcast worked only when the was in foreground/minimize but not didn't work when the app was killed:
         * https://stackoverflow.com/questions/44149921/broadcast-receiver-not-working-when-app-is-closed/60197247
         *
         * In short summary -> when launch via debug mode and then we swipe to close the app, android consider it as Force Stop and unregister the receivers.
         * It won't be happening if we launch the app ourselves
         *
         *
         * TODO: Move to function
         *
         * **/
        val alarmManager: AlarmManager =
            activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(activity, RemainderReceiver::class.java)
        intent.action = reminder.toString()
        intent.putExtra(
            "articleUrl",
            articleListRecyclerViewAdapter.getCurrentURL(position)
        )
        intent.putExtra(
            "articleTitle",
            articleListRecyclerViewAdapter.getCurrentTitle(position)
        )

        //TODO: HANDLE NULL
        val pintent = PendingIntent.getBroadcast(
            activity,
            1,
            intent,
            FLAG_UPDATE_CURRENT
        ) // Pending intent docs about the flag - Flag indicating that if the described PendingIntent already exists, then keep it but replace its extra data with what is in this new Intent.

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pintent)
        val currentArticle = articleListRecyclerViewAdapter.getCurrentArticle(position)
        currentArticle?.let {
            articlesViewModel.insertReminder(it, reminder)
        }
    }

//    override fun onReminderIconClick() {
//        uiCommunicationListener.onResponseReceived(
//            title = "Reminder",
//            message = "You have alarm set to October",
//            uiComponentType = UIComponentType.ReminderDialog(
//                callbacks = object : ReminderDialogCallbacks {
//                    override fun changeReminder() {
//                        Log.d(TAG, "changeReminder: ")
//                    }
//
//                    override fun cancelReminder() {
//                        Log.d(TAG, "cancelReminder: ")
//                    }
//                }
//            )
//        )
//    }

    override fun onReminderIconClick(text: String, position: Int) {

        uiCommunicationListener.onResponseReceived(
            Response(
                title = "ARTICLE REMINDER",
                message = "You have alarm set to $text",
                uiComponentType = UIComponentType.ReminderDialog(
                    object : ReminderDialogCallbacks {
                        override fun changeReminder() {
                            setReminder(position)
                        }

                        override fun cancelReminder() {
                            cancelExistingReminder(position)
                        }
                    }
                )
            )
        )

//
//        uiCommunicationListener.onResponseReceived(
//            title = "Reminder",
//            message = "You have alarm set to October",
//            uiComponentType = UIComponentType.ReminderDialog(
//                callbacks = object : ReminderDialogCallbacks {
//                    override fun changeReminder() {
//                        Log.d(TAG, "changeReminder: ")
//                    }
//
//                    override fun cancelReminder() {
//                        Log.d(TAG, "cancelReminder: ")
//                    }
//                }
//            )
//        )
    }

    override fun onAddNoteClick(adapterPosition: Int) {
        val articleTitle = articleListRecyclerViewAdapter.getCurrentURL(adapterPosition)
//        val articlesURLList = articleListRecyclerViewAdapter.getArticlesUrlList(position)
        articleTitle?.let {
            val action =
                ArticlesFragmentDirections.actionArticlesFragmentToNoteFragment(it, null)
            findNavController().navigate(action)
        }
//        val articleTitle = articleListRecyclerViewAdapter.getCurrentTitle(adapterPosition)
////        val articlesURLList = articleListRecyclerViewAdapter.getArticlesUrlList(position)
//        articleTitle?.let {
//            val action =
//                ArticlesFragmentDirections.actionArticlesFragmentToNoteFragment(it)
//            findNavController().navigate(action)
//        }
    }

    override fun onViewNotesClick(adapterPosition: Int) {
        val articleURL = articleListRecyclerViewAdapter.getCurrentURL(adapterPosition)
        articleURL?.let {
            val action =
                ArticlesFragmentDirections.actionArticlesFragmentToNoteListFragment(it)
            findNavController().navigate(action)
        }
    }

    override fun onDeleteArticle(adapterPosition: Int) {
        articleListRecyclerViewAdapter.getCurrentArticle(adapterPosition)?.let {
            showUndoSnackbar(adapterPosition, it)
        } ?: uiCommunicationListener.onResponseReceived(
            response = Response(
                title = "Error",
                message = "Unable to delete the article right now",
                uiComponentType = UIComponentType.Toast
            )
        )
    }

    private fun cancelExistingReminder(position: Int) {

        val article = articleListRecyclerViewAdapter.getCurrentArticle(position)

        article?.let {
            val alarmManager: AlarmManager =
                activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(activity, RemainderReceiver::class.java)
            intent.action = it.reminder.toString()
            intent.putExtra(
                "articleUrl",
                articleListRecyclerViewAdapter.getCurrentURL(position)
            )//TODO: HANDLE NULL
            val pintent = PendingIntent.getBroadcast(
                activity,
                1,
                intent,
                FLAG_UPDATE_CURRENT
            ) // Pending intent docs about the flag - Flag indicating that if the described PendingIntent already exists, then keep it but replace its extra data with what is in this new Intent.
            alarmManager.cancel(pintent)
            articlesViewModel.cancelReminder(it.url)
        }


    }

    private fun setReminder(position: Int) {
        DateTimeManager.launchDateTimeDialogs(binding.root.context,
            object : ReminderListeners {
                override fun onSuccess(reminder: Instant, hour: Int, minute: Int, day: Int) {
                    onReminderSet(
                        position,
                        reminder,
                        hour,
                        minute,
                        day
                    )

                }

            }
        )
    }

    /**
     *
     * END OF RELATED FUNCTIONS EXPLANATION
     *
     * **/


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        Log.d(TAG, "onCreateOptionsMenu: ")
        inflater.inflate(R.menu.article_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.filter_all -> {
                item.isChecked = true
                currentFilterMode = FilterMode.ALL
                Log.d(TAG, "onOptionsItemSelected: $item is checked ${item.isChecked}")
                lifecycleScope.launch {
//                    articlesViewModel.getArticles(args.domainName, FilterMode.ALL)
                    articlesViewModel.getAll(FilterMode.ALL, args.domainName)
                }
                saveFilter(FilterMode.ALL)

//                saveToSharedPrefs(0)
                return true
            }
            R.id.filter_already_read -> {
                item.isChecked = true
                currentFilterMode = FilterMode.ALREADY_READ

                Log.d(TAG, "onOptionsItemSelected: $item is checked ${item.isChecked}")

                lifecycleScope.launch {
//                    articlesViewModel.getArticles(args.domainName, FilterMode.ALREADY_READ)
//                    articlesViewModel.getArticles(args.domainName, FilterMode.ALREADY_READ)
                    articlesViewModel.getAll(FilterMode.ALREADY_READ, args.domainName)


                }
                saveFilter(FilterMode.ALREADY_READ)

//                saveToSharedPrefs(1)


                return true
            }
            R.id.filter_no_read -> {
                item.isChecked = true
                currentFilterMode = FilterMode.NOT_READ

                Log.d(TAG, "onOptionsItemSelected: $item is checked ${item.isChecked}")

                lifecycleScope.launch {
//                    articlesViewModel.getArticles(args.domainName, FilterMode.NOT_READ)
                    articlesViewModel.getAll(FilterMode.NOT_READ, args.domainName)

                }
                saveFilter(FilterMode.NOT_READ)


//                saveToSharedPrefs(2)

                return true
            }
//            R.id.order_by_name -> {
//                item.isChecked = true
//                return true
//            }
//            R.id.order_by_date -> {
//                item.isChecked = true
//                return true
//            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveFilter(filterMode: FilterMode) {
        articlesViewModel.saveFilter(filterMode, args.domainName)
    }

    /**
     *
     * When the menu being populated, we need to know which filter user chose(default is 0 - ALL)
     *
     * **/
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

//        val sharedPref =
//            activity?.getSharedPreferences(
//                getString(R.string.filter_mode_prefs),
//                Context.MODE_PRIVATE
//            )

        //        sharedPref?.let {
//            filterMode = sharedPref.getInt(getString(R.string.filter_mode_value), 0)
//        }
//        Log.d(TAG, "getArticles: $filterMode")
        when (currentFilterMode) {
            FilterMode.ALREADY_READ -> {
                Log.d(TAG, "onPrepareOptionsMenu: ${args.filterMode}")
                menu.findItem(R.id.filter_already_read).isChecked = true
            }
            FilterMode.NOT_READ -> {
                Log.d(TAG, "onPrepareOptionsMenu: ${args.filterMode}")

                menu.findItem(R.id.filter_no_read).isChecked = true
            }
            FilterMode.ALL -> {
                Log.d(TAG, "onPrepareOptionsMenu: ${args.filterMode}")

                menu.findItem(R.id.filter_all).isChecked = true
            }
        }
    }

    /**
     *
     * Saving user filter options
     *
     * **/
    private fun saveToSharedPrefs(enumMirror: Int) {
//        Log.d(TAG, "saveToSharedPrefs: $enumMirror")
//
//        val sharedPref =
//            activity?.getSharedPreferences(
//                getString(R.string.filter_mode_prefs),
//                Context.MODE_PRIVATE
//            )
//
//        sharedPref?.let {
////            with(sharedPref.edit()) {
////                putInt(getString(R.string.filter_mode_value), enumMirror)
////                /**
////                 *
////                 * SharedPrefs docs:
////                 *
////                 * apply() changes the in-memory SharedPreferences object immediately but writes the updates to disk asynchronously.
////                 * Alternatively, you can use commit() to write the data to disk synchronously.
////                 *
////                 * But because commit() is synchronous, you should avoid calling it from your main thread because it could pause your UI rendering.
////                 *
////                 * **/
////                apply()
////            }
//            it.edit().putInt(getString(R.string.filter_mode_value), enumMirror).apply()
//
//        }


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
//            uiCommunicationListener.onResponseReceived("Undo action", UIComponentType.Toast)
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
//            uiCommunicationListener.onResponseReceived(
//                "You already mark it as already read",
//                UIComponentType.Toast
//            )

//            uiCommunicationListener.onResponseReceived(
//                message =
//                "You already mark it as already read",
//                uiComponentType =
//                UIComponentType.Toast
//            )

            uiCommunicationListener.onResponseReceived(
                response = Response(
                    message = "You already mark it as already read",
                    uiComponentType = UIComponentType.Toast
                )
            )
            articleListRecyclerViewAdapter.notifyItemChanged(position)

        }


    }


}