package com.example.shareway.ui.onBoarding.screens

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.example.shareway.R
import kotlinx.android.synthetic.main.fragment_first_screen.*
import kotlinx.android.synthetic.main.fragment_thrid_screen.*


class ThirdScreen : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_thrid_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        finishButton.setOnClickListener {
            setOnBoardingFinished()
            findNavController().navigate(R.id.action_viewPagerFragment_to_categoriesFragment)
        }
    }

    private fun setOnBoardingFinished(){
        /**
         * TODO: EXTRACT TAG
         *
         * **/
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("finishedOnBoarding", true)
        editor.apply()
    }


}