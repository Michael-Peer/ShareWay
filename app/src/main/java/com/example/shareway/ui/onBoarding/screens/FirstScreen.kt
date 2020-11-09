package com.example.shareway.ui.onBoarding.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.shareway.R
import kotlinx.android.synthetic.main.fragment_first_screen.*

class FirstScreen : Fragment() {

    companion object {
        private const val TAG = "FirstScreen"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        Log.d(TAG, "onCreateView: $container")
//
//        val vp = container as ViewPager2
//        Log.d(TAG, "onCreateView: $vp")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * From fragment, call getActivity() which will gives you the activity in which the fragment is hosted. Then call findViewById(ViewPagerId) to get the ViewPager.
         *
         *
         * **/
        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager2)

        Log.d(TAG, "onViewCreated: activity $activity")
        Log.d(TAG, "onViewCreated: $viewPager")

        nextButtonFirst.setOnClickListener {
            Log.d(TAG, "onViewCreated: clicked ${viewPager!!.currentItem}")
            viewPager?.apply {
                currentItem = 1
            }
        }


    }


}