package com.example.shareway

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.example.shareway.databinding.ActivityMainBinding
import com.example.shareway.listeners.ReminderDialogCallbacks
import com.example.shareway.listeners.Response
import com.example.shareway.listeners.UICommunicationListener
import com.example.shareway.ui.ArticlesFragment
import com.example.shareway.utils.UIComponentType
import com.example.shareway.utils.extensions.currentNavigationFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi


class MainActivity : AppCompatActivity(), UICommunicationListener {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")

        binding = ActivityMainBinding.inflate(layoutInflater)

//        setContentView(R.layout.activity_main)
        setContentView(binding.root)

        setupAppBar()

    }

    private fun setupAppBar() {
        setSupportActionBar(binding.toolbar)

        supportFragmentManager.findFragmentById(R.id.nav_host)?.findNavController()?.let {
            appBarConfiguration = AppBarConfiguration(it.graph)
            setupActionBarWithNavController(it, appBarConfiguration)
        }
    }

    override fun displayProgressBar(isLoading: Boolean) {
        Log.d(TAG, "displayProgressBar: ")
        if (isLoading) binding.progressBar.visibility =
            View.VISIBLE
        else binding.progressBar.visibility =
            View.GONE
    }

    override fun displayProgressIndicator(progress: Int) {
        Log.d(TAG, "displayProgressIndicator: $progress")
        if (progress < 100) {
            binding.progressIndicator.visibility = View.VISIBLE
            binding.progressIndicator.setProgressCompat(progress, true)
        } else {
            binding.progressIndicator.visibility = View.GONE

        }
    }

    override fun onResponseReceived(response: Response) {
        when (response.uiComponentType) {
            is UIComponentType.Toast -> {
                Toast.makeText(this, response.message ?: "Error", Toast.LENGTH_LONG).show()
            }

            is UIComponentType.ReminderDialog -> {
                displayReminderDialog(
                    response.title,
                    response.message,
                    response.uiComponentType.callbacks
                )
            }
        }
    }

//    override fun onResponseReceived(
//        title: String? ,
//        message: String,
//        uiComponentType: UIComponentType
//    ) {
//        when (uiComponentType) {
//            is UIComponentType.Dialog -> {
//                //TODO: Display dialog
////                displayDialog(message)
//            }
//            is UIComponentType.Toast -> {
//                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
//            }
//
//            is UIComponentType.ReminderDialog -> {
//                displayDialog(title, message, uiComponentType.callbacks)
//            }
//        }
//    }


//    override fun onResponseReceived(message: String, uiComponentType: UIComponentType) {
//
//
//        when (uiComponentType) {
//            is UIComponentType.Dialog -> {
//                //TODO: Display dialog
//                displayDialog(message)
//            }
//            is UIComponentType.Toast -> {
//                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
//            }
//        }
//    }

    private fun displayReminderDialog(
        title: String?,
        message: String?,
        callbacks: ReminderDialogCallbacks
    ) {
        MaterialDialog(this).show {
            title(text = title)
            message(text = message)
            positiveButton(text = "CHANGE") {
                callbacks.changeReminder()
            }
            negativeButton(text = "CANCEL") {
                callbacks.cancelReminder()
            }
        }
    }


    /**
     *
     *
     * TODO: In case of more dialogs in the app, create general Dialog with different option(e.g error, info etc)
     *
     * **/
//    private fun displayDialog(title: String, message: String) {
//        Log.d(TAG, "displayDialog: ")
//        MaterialDialog(this)
//            .show {
//                title(text = title)
//                message(text = message)
//            }
//    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(findNavController(R.id.nav_host), appBarConfiguration)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    /**
     *
     * Handle the back button pressing.
     * we search for the top(current) fragment, and decide what to do based on the fragment
     *
     * **/
    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    override fun onBackPressed() {
        super.onBackPressed()
        val currentFragment = supportFragmentManager.currentNavigationFragment

        when (currentFragment) {
            is ArticlesFragment -> {
                currentFragment.onBackButtonPressed()
            }
        }
    }

    var mTouchOutsideView: View? = null
    var onTouchOutsideViewListener: OnTouchOutsideViewListener? = null

    /**
     * Sets a listener that is being notified when the user has tapped outside a given view. To remove the listener,
     * call [.removeOnTouchOutsideViewListener].
     *
     *
     * This is useful in scenarios where a view is in edit mode and when the user taps outside the edit mode shall be
     * stopped.
     *
     * @param view
     * @param onTouchOutsideViewListener
     */
    fun setOnTouchOutsideViewListener(
        view: View?,
        onTouchOutsideViewListener: OnTouchOutsideViewListener
    ) {
        mTouchOutsideView = view
        this.onTouchOutsideViewListener = onTouchOutsideViewListener
    }

    /**
     *
     * Here we determine if the touch was outside of a given visible view.
     * If it is is - we send the view and the event to [OnTouchOutsideViewListener] interface
     *
     * **/
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN || ev.action == MotionEvent.ACTION_UP) {
            // Notify touch outside listener if user tapped outside a given view
            if (onTouchOutsideViewListener != null && mTouchOutsideView != null && mTouchOutsideView!!.visibility == View.VISIBLE
            ) {
                val viewRect = Rect()
                mTouchOutsideView!!.getGlobalVisibleRect(viewRect)
                if (!viewRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    onTouchOutsideViewListener!!.onTouchOutside(mTouchOutsideView, ev)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * Interface definition for a callback to be invoked when a touch event has occurred outside a formerly specified
     * view. See [.setOnTouchOutsideViewListener]
     */
    interface OnTouchOutsideViewListener {
        /**
         * Called when a touch event has occurred outside a given view.
         *
         * @param view  The view that has not been touched.
         * @param event The MotionEvent object containing full information about the event.
         */
        fun onTouchOutside(view: View?, event: MotionEvent?)

    }


}

