package com.example.shareway.ui.splash

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.shareway.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SplashScreenFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.hide()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        /**
         *
         * TODO: Replace dummy delay with initialization
         *
         * **/
        Handler().postDelayed({
            if (!isOnBoardingFinished()) findNavController().navigate(R.id.action_splashScreenFragment_to_viewPagerFragment) else findNavController().navigate(
                R.id.action_splashScreenFragment_to_categoriesFragment
            )

        }, 3000)


        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    private fun isOnBoardingFinished(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("finishedOnBoarding", false)
    }
}