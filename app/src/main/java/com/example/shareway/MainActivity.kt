package com.example.shareway

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.shareway.databinding.ActivityMainBinding
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

    override fun onResponseReceived(message: String, uiComponentType: UIComponentType) {
        when (uiComponentType) {
            is UIComponentType.Dialog -> {
                //TODO: Display dialog
            }
            is UIComponentType.Toast -> {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

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


}