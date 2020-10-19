package com.example.shareway.ui.onBoarding.viewPager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(private val onBoardingFragmentsList: List<Fragment>, fm:FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle){

    override fun getItemCount(): Int {
       return onBoardingFragmentsList.size
    }

    override fun createFragment(position: Int): Fragment {
        return onBoardingFragmentsList[position]
    }

}