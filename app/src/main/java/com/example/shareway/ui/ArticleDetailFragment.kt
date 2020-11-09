package com.example.shareway.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.shareway.MainActivity
import com.example.shareway.R
import com.example.shareway.databinding.FragmentArticleDetailBinding
import com.example.shareway.listeners.UICommunicationListener
import com.example.shareway.utils.animation.FabAnimation
import com.example.shareway.utils.views.CustomWebChromeClient
import com.example.shareway.utils.views.CustomWebview
import com.example.shareway.viewmodels.ArticlesViewModel
import kotlinx.android.synthetic.main.fragment_article_detail.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class ArticleDetailFragment : Fragment() {
    var x = 0f
    var y = 0f
    val SCROLL_THRESHOLD = 3.5f


    companion object {
        private const val TAG = "ArticleDetailFragment"
    }

    private lateinit var uiCommunicationListener: UICommunicationListener
    private lateinit var binding: FragmentArticleDetailBinding
    private lateinit var nextButtonApp: MenuItem
    private lateinit var preButtonApp: MenuItem

    private val args: ArticleDetailFragmentArgs by navArgs()

    private var menuOpen = false
    private var isSearchMenuOpen = false
    private var searchQuery = ""
//    private var isDesktopMode = false

    private val articleViewModel: ArticlesViewModel by viewModel()

    private var shouldRestoreState: Boolean = false
    var bundle: Bundle? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        shouldRestoreState = savedInstanceState?.let {
            bundle = savedInstanceState
            menuOpen = savedInstanceState.getBoolean("isMenuOpen")
            isSearchMenuOpen = savedInstanceState.getBoolean("isSearchMenuOpen")
            searchQuery = savedInstanceState.getString("searchQuery") ?: ""
            true
        } ?: false



        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(TAG, "handleOnBackPressed: ")
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    findNavController().popBackStack()
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return FragmentArticleDetailBinding.inflate(inflater, container, false).apply {
            binding = this
        }.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//val webc = CustomWebChromeClient()


        if (shouldRestoreState && bundle != null) {
//            binding.webView.restoreState(bundle)
            binding.webView.webChromeClient =
                object : CustomWebChromeClient(activity as AppCompatActivity) {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        uiCommunicationListener.displayProgressIndicator(newProgress)
                    }

                    override fun onReceivedTitle(view: WebView?, title: String?) {
                        super.onReceivedTitle(view, title)
                        Log.d(TAG, "onReceivedTitle: $title")
                    }

                    override fun onReceivedTouchIconUrl(
                        view: WebView?,
                        url: String?,
                        precomposed: Boolean
                    ) {
                        super.onReceivedTouchIconUrl(view, url, precomposed)
                        Log.d(TAG, "onReceivedTouchIconUrl: $url")
                    }

                    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                        super.onReceivedIcon(view, icon)
                        Log.d(TAG, "onReceivedIcon: ")
                    }
                }

        } else {
            binding.webView.apply {
                webViewClient = WebViewClient()
                webChromeClient = object : CustomWebChromeClient(activity as AppCompatActivity) {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        uiCommunicationListener.displayProgressIndicator(newProgress)
                    }

                    override fun onReceivedTitle(view: WebView?, title: String?) {
                        super.onReceivedTitle(view, title)
                        Log.d(TAG, "onReceivedTitle: $title")
                    }

                    override fun onReceivedTouchIconUrl(
                        view: WebView?,
                        url: String?,
                        precomposed: Boolean
                    ) {
                        super.onReceivedTouchIconUrl(view, url, precomposed)
                        Log.d(TAG, "onReceivedTouchIconUrl: $url")
                    }

                    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                        super.onReceivedIcon(view, icon)
                        Log.d(TAG, "onReceivedIcon: ")
                    }
                }
                settings.javaScriptEnabled = true
                /**
                 *
                 * we need to chagne the UA because google restriction, 403 disallowed user agent when trying to login to some services
                 * **/
                val ua = settings.userAgentString
                settings.userAgentString = ua.replace("wv", "")

                loadUrl(args.articlesUrl)


            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val sab = (activity as AppCompatActivity?)?.supportActionBar
            binding.webView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->




                //Down
                if (scrollY > oldScrollY && scrollY > 0) {

                    if (menuOpen) {
                        binding.apply {
                            shareFab.hide()
                            markAsReadFab.hide()
                            reminderFab.hide()
                        }
                    }
                    binding.mainFab.hide();
                }

                //Up
                if (scrollY < oldScrollY) {
                    if (menuOpen) {
                        binding.apply {
                            shareFab.show()
                            markAsReadFab.show()
                            reminderFab.show()
                        }
                    }

                    binding.mainFab.show();
                }
            }
        } else {
            binding.webView.scrollListener = object : CustomWebview.WebViewScrollListener {
                override fun onScroll(
                    scrollX: Int,
                    scrollY: Int,
                    oldScrollX: Int,
                    oldScrollY: Int
                ) {
//                Log.d(TAG, "onScroll: ")
                }

                override fun onScrollDown() {
                    Log.d(TAG, "onScrollDown: ")
                    if (menuOpen) {
                        binding.apply {
                            shareFab.hide()
                            markAsReadFab.hide()
                            reminderFab.hide()
                        }
                    }
                    binding.mainFab.hide()
                }

                override fun onScrollUp() {
                    if (menuOpen) {
                        binding.apply {
                            shareFab.show()
                            markAsReadFab.show()
                            reminderFab.show()
                        }
                    }
                    binding.mainFab.show()
                }

            }
        }

        setOutsideViewListener()

        if (!menuOpen) initFabs()

        handleFabClicks()
        handleMiniFabClicks()

    }


    /**
     *
     * In this function we set listener to detect outside clicks of a given view
     * we register view & listener to var & interface inside main activity, and we get notify when outside click is detected.
     * We need this func in order to close fab menu on outside click.
     *
     * Than, we need to differentiate between to states - scroll VS tap.
     * We need to do this because we have another listener(custom webView) that detect webView scrolling in order to show/hide fabs on scroll, and they're overlap each other, for example:
     * I have a logic of show/hide all fabs on scroll, and because [setOutsideViewListener] detect every outside touch(doesn't matter if scroll or touch) it's just messes up everything.
     *
     * Basically the differnces are like this:
     *
     * On scroll - hide/show all the fabs, including the main. if menu open, keep it open
     *
     * on Tap outside - hide the mini fabs, if menu open - close it
     *
     *
     * Initially I thought to tackle this problem by calculating the time between user tapped to the regular tap time [ViewConfiguration.getTapTimeout()] and decide by that if user is just tapping or scrollimg.
     * It is working solution BUT, sometimes the user just fast scrolling, and because the user just fling his finger fast, its been detected as tap, not scroll.
     *
     * So I took another approach - I'm getting the x/y on [MotionEvent.ACTION_DOWN], which mean when the user put his finger on the scree,.
     * Then in [MotionEvent.ACTION_UP] when user release his finger, i'm checking fixed screen pixel move [SCROLL_THRESHOLD].
     *https://medium.com/@mattgrint/detecting-clicks-or-scrolls-in-kotlin-for-android-9c2cf0aab7fa
     *
     *
     *
     * **/
    private fun setOutsideViewListener() {
        val mainAct: MainActivity? = activity as MainActivity
        mainAct?.let { mainActivity ->
            mainActivity.mTouchOutsideView = binding.mainFab
            mainActivity.onTouchOutsideViewListener =
                object : MainActivity.OnTouchOutsideViewListener {
                    override fun onTouchOutside(view: View?, event: MotionEvent?) {
                        event?.let {
                            when (it.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    x = it.x
                                    y = it.y
                                }
                                MotionEvent.ACTION_UP -> {
                                    if (abs(x - it.x) < SCROLL_THRESHOLD || abs(y - it.y) < SCROLL_THRESHOLD) {
                                        Log.d(TAG, "onTouchOutside: click")
                                        closeFabsIfOpen()
                                    } else {
                                        Log.d(TAG, "onTouchOutside: scroll ")
                                    }
                                }
                                else -> {
                                }
                            }
                        }
                    }
                }
        }

//                if (event?.action == MotionEvent.ACTION_DOWN) {
//                    startClickTime = System.currentTimeMillis()
//                }
//                if (event?.action == MotionEvent.ACTION_UP) {
//                    if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {
//                        // Touch was a simple tap.
//                        Log.d(TAG, "onTouchOutside: 1")
//                    } else {
//                        // Touch was a not a simple tap.
//                        Log.d(TAG, "onTouchOutside: 2")
//
//                    }
//                }
    }

    private fun closeFabsIfOpen() {

        if (menuOpen) {
            Log.d(TAG, "onTouchOutside MENU OPEN")
            menuOpen = FabAnimation.rotateFab(binding.mainFab, !menuOpen)
            binding.apply {
                FabAnimation.apply {
                    hideFabMenu(binding.shareFab, 800)
                    hideFabMenu(binding.markAsReadFab, 600)
                    hideFabMenu(binding.reminderFab, 400)
                }
            }
        }
    }

    private fun initFabs() {
        binding.apply {
            FabAnimation.initFabMenu(shareFab)
            FabAnimation.initFabMenu(markAsReadFab)
            FabAnimation.initFabMenu(reminderFab)

//            shareFab.visibility = View.GONE
//            markAsReadFab.visibility = View.GONE
//            reminderFab.visibility = View.GONE
        }
    }

    private fun animateFabs() {

//        binding.apply {
//
////            mainFab.animate()
////                .rotation(720f)
////                .setDuration(2000)
//
//           menuOpen =  FabAnimation.rotateFab(binding.mainFab, !menuOpen)
//
//            shareFab.apply {
//                alpha = 0f
//                visibility = View.VISIBLE
//                animate()
//                    .alpha(1f)
//                    .setDuration(1000)
//                    .setListener(null)
//            }
//
//            markAsReadFab.apply {
//                alpha = 0f
//                visibility = View.VISIBLE
//                animate()
//                    .alpha(1f)
//                    .setDuration(1400)
//                    .setListener(null)
//            }
//            reminderFab.apply {
//                alpha = 0f
//                visibility = View.VISIBLE
//                animate()
//                    .alpha(1f)
//                    .setDuration(1600)
//                    .setListener(null)
//            }
//        }

    }

    private fun handleFabClicks() {
        mainFab.setOnClickListener {
            animateFabs()
            menuOpen = FabAnimation.rotateFab(it, !menuOpen)
            Log.d(TAG, "handleFabClicks: $menuOpen")
            if (menuOpen) {
                FabAnimation.apply {
//                    showFabMenu(binding.textView,400)
                    showFabMenu(binding.shareFab, 400)
                    showFabMenu(binding.markAsReadFab, 600)
                    showFabMenu(binding.reminderFab, 800)
                }
            } else {
                FabAnimation.apply {
//                    hideFabMenu(binding.textView,400)

                    hideFabMenu(binding.shareFab, 800)
                    hideFabMenu(binding.markAsReadFab, 600)
                    hideFabMenu(binding.reminderFab, 400)
                }
            }
        }


//        shareFloatingActionButton.setOnClickListener {
//            val url = args.articlesUrl
////            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
////            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            val intent = Intent(Intent.ACTION_SEND)
//            intent.setType("text/plain")
//            intent.putExtra(Intent.EXTRA_TEXT, args.articlesUrl)
//            startActivity(Intent.createChooser(intent, "SHARETHIS"))
//            /**
//             *
//             * FAN ICON WITH OPEN IN THE WEB, SAHRE IN MAIl WHATS UP ETC/
//             *
//             * Maybe checkbox in the app bar that user can decide if show/hide the share button
//             *
//             * **/
////            requireContext().startActivity(intent)
//        }

    }

    private fun handleMiniFabClicks() {
        binding.apply {
            shareFab.setOnClickListener {
                val intent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TITLE, "Look!")
                    putExtra(Intent.EXTRA_TEXT, args.articlesUrl)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(intent, "LOOK AT THIS WOW")
                startActivity(shareIntent)
            }

            markAsReadFab.setOnClickListener {
                articleViewModel.updateAlreadyRead(args.articlesUrl)
            }

            reminderFab.setOnClickListener {
                //TODO: Handle day bug first
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)

        val menuItem = menu.findItem(R.id.menu_search)
        val searchView = menuItem.actionView as SearchView


        nextButtonApp = menu.findItem(R.id.menu_search_next)
        preButtonApp = menu.findItem(R.id.menu_search_previous)
//        searchView.onActionViewExpanded()
//        searchView.isIconified = false
//        searchView.setIconifiedByDefault(false)
//        searchView.icon

        Log.d(TAG, "onCreateOptionsMenu: ${searchView.isIconified}")
        Log.d(TAG, "onCreateOptionsMenu: isSearchMenuOpen $isSearchMenuOpen")

        searchView.maxWidth = Int.MAX_VALUE
        if (isSearchMenuOpen) {
            nextButtonApp.isVisible = true
            preButtonApp.isVisible = true
            isSearchMenuOpen = true
            searchView.setQuery(searchQuery, false)
//            searchView.onActionViewExpanded()
            searchView.isIconified = false
            (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(false) // TODO: Realese on back button
        }

        searchView.setOnSearchClickListener {
            Log.d(TAG, "onCreateOptionsMenu: onOptionsItemSelected")
            nextButtonApp.isVisible = true
            preButtonApp.isVisible = true
            isSearchMenuOpen = true
            Log.d(
                TAG,
                "onCreateOptionsMenu: isSearchMenuOpen $isSearchMenuOpen searchView.setOnSearchClickListener"
            )
            (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(false) // TODO: Realese on back button
        }


        searchView.setOnCloseListener {
            nextButtonApp.isVisible = false
            preButtonApp.isVisible = false
            isSearchMenuOpen = false
            Log.d(TAG, "onCreateOptionsMenu: $isSearchMenuOpen earchView.setOnCloseListene")
            (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true) // TODO: Realese on back button
            false
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.i(TAG, "onQueryTextChange: onQueryTextSubmit" + query);
                binding.webView.findAllAsync(query ?: "")
                try {
                    val method =
                        WebView::class.java.getMethod("setFindIsUp", java.lang.Boolean.TYPE)
                    method.invoke(binding.webView, true)
                } catch (e: Exception) {
                    Log.d(TAG, "onQueryTextSubmit: $e")
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.i(TAG, "onQueryTextChange: onQueryTextChange" + newText);
                searchQuery = newText ?: ""

                /**
                 *
                 * Send the search input to the filter function inside the adapter
                 *
                 * **/
                return false; }
        })
    }


    /**
     *
     *
     * Desktop/Mobile mode
     *
     * **/
    private fun setViewMode(isDesktopMode: Boolean) {
        var userAgent = ""
        val mobileStr = "Mobile"
        val androidStr = "Android"
        userAgent = if (isDesktopMode) {
            binding.webView.settings.userAgentString
                .replace(mobileStr, mobileStr.reversed())
                .replace(androidStr, androidStr.reversed())

        } else {
            binding.webView.settings.userAgentString
                .replace(mobileStr.reversed(), mobileStr)
                .replace(androidStr.reversed(), androidStr)
        }

        binding.webView.settings.apply {
            userAgentString = userAgent
            useWideViewPort = isDesktopMode
            loadWithOverviewMode = isDesktopMode
            setSupportZoom(isDesktopMode)
            builtInZoomControls = isDesktopMode
        }
        binding.webView.reload()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /**
         *
         * Search next/previous word.
         * Clicks can only happen while in search mode
         *
         * **/
        when (item.itemId) {
            R.id.menu_search_next -> {
                Log.d(TAG, "onOptionsItemSelected: menu_search_next")
                binding.webView.findNext(true)
            }
            R.id.menu_search_previous -> {
                Log.d(TAG, "onOptionsItemSelected: menu_search_previous")
                binding.webView.findNext(false)

            }
            R.id.desktop_mode -> {
                item.title = if (item.title == "Desktop") {
                    setViewMode(isDesktopMode = true)
                    "Mobile"
                } else {
                    setViewMode(isDesktopMode = false)
                    "Desktop"
                }
            }
            R.id.open_in_web -> {
                //TOOD: Safe intent
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(args.articlesUrl))
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     *
     * Fragments do not have an onRestoreInstanceState method.
     * We can achieve the same result in onActivityCreated, which receives a bundle with the saved instance state (or null).
     *
     *
     * **/
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.webView.restoreState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isMenuOpen", menuOpen)
        outState.putBoolean("isSearchMenuOpen", isSearchMenuOpen)
        outState.putString("searchQuery", searchQuery)
        binding.webView.saveState(outState)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            uiCommunicationListener = context as UICommunicationListener
        } catch (e: ClassCastException) {
            Log.d(TAG, "$context must implement UICommunicationListener")
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }


}