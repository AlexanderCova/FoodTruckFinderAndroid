package com.foodtruckfindermi.client.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.foodtruckfindermi.client.Fragments.MapsFragment
import com.foodtruckfindermi.client.Fragments.TruckInfoFragment
import com.foodtruckfindermi.client.Fragments.TruckReviewsFragment

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