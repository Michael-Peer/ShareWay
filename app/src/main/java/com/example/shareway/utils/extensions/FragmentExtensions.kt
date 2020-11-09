package com.example.shareway.utils.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 *
 * Find the top(current) fragment
 *
 * **/
val FragmentManager.currentNavigationFragment: Fragment?
    get() = primaryNavigationFragment?.childFragmentManager?.fragments?.first()