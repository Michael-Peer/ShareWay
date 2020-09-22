package com.example.shareway.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.shareway.databinding.FragmentArticleDetailBinding
import com.example.shareway.listeners.UICommunicationListener
import kotlinx.android.synthetic.main.fragment_article_detail.*


class ArticleDetailFragment : Fragment() {

    companion object {
        private const val TAG = "ArticleDetailFragment"
    }

    private lateinit var uiCommunicationListener: UICommunicationListener
    private lateinit var binding: FragmentArticleDetailBinding

    private val args: ArticleDetailFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(args.articleUrl)
        }

        handleFabClicks()
    }

    private fun handleFabClicks() {
        shareFloatingActionButton.setOnClickListener {
            val url = args.articleUrl
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            /**
             *
             * FAN ICON WITH OPEN IN THE WEB, SAHRE IN MAIl WHATS UP ETC/
             *
             * Maybe checkbox in the app bar that user can decide if show/hide the share button
             *
             * **/
            requireContext().startActivity(intent)
        }

    }
}