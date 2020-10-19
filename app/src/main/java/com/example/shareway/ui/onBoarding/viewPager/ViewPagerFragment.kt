package com.example.shareway.ui.onBoarding.viewPager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shareway.R
import com.example.shareway.ui.onBoarding.screens.FirstScreen
import com.example.shareway.ui.onBoarding.screens.SecondScreen
import com.example.shareway.ui.onBoarding.screens.ThirdScreen
import kotlinx.android.synthetic.main.fragment_view_pager.view.*

class ViewPagerFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)

        val onBoardingFragmentList = listOf(
            FirstScreen(),
            SecondScreen(),
            ThirdScreen()
        )

        val viewPagerAdapter = ViewPagerAdapter(
            onBoardingFragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        view.viewPager2.adapter = viewPagerAdapter

        return view
    }

}