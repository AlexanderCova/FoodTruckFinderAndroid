package com.example.youfood.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.youfood.Fragments.MapsFragment
import com.example.youfood.Fragments.TruckInfoFragment
import com.example.youfood.Fragments.TruckReviewsFragment

internal class TruckPagerAdapter(fm: FragmentManager?) :
    FragmentPagerAdapter(fm!!){

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> {
                TruckInfoFragment()
            }
            1 -> {
                MapsFragment()
            }
            2 -> {
                TruckReviewsFragment()
            }

            else -> {
                TruckInfoFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

}