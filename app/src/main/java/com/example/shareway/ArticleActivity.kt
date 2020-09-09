package com.example.shareway

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.shareway.viewmodels.CategoriesViewModel
import kotlinx.android.synthetic.main.activity_job.*
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArticleActivity : AppCompatActivity() {

    companion object {
        private const val PLAIN_TEXT_MIME = "text/plain"
        private const val TAG = "ArticleActivity"
    }

    private val categoriesViewModel: CategoriesViewModel by viewModel()

    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_job)

        getIntentFromUser()
    }



    private fun getIntentFromUser() {
        intent?.let { intent: Intent ->
            if (intent.action == Intent.ACTION_SEND) {
                if (intent.type == PLAIN_TEXT_MIME) {
                    handleIntent()
                }
            }
        } ?: finish() //TODO: let user know
    }

    private fun handleIntent() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "onCreate: in io")
            handleUrlFromIntent(intent)
            Log.d(TAG, "onCreate: after handle text")
            delay(2000)
            Log.d(TAG, "onCreate: after delay")
            withContext(Dispatchers.Main) {

//                val serviceIntent = Intent(this@JobActivity, WorkOnArticleService::class.java)
//
//                WorkOnArticleService.enqueueWork(this@JobActivity, serviceIntent)

                url?.let { url ->
                    categoriesViewModel.manipulateArticleUrl(url)
                }

                progressBar.visibility = View.GONE

                Log.d(TAG, "onCreate: in main")
                finish()
            }
        }
    }

    private fun handleUrlFromIntent(intent: Intent) {
        intent.apply {
            getStringExtra(Intent.EXTRA_TEXT)?.let {
                Log.d(TAG, "handleTextIntent: $it")
                url = it
            }
        }
    }
}
