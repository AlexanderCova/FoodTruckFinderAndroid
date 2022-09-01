package com.foodtruckfindermi.client.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.foodtruckfindermi.client.Fragments.EventInfoFragment
import com.foodtruckfindermi.client.Fragments.EventTrucksFragment

internal class EventPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!){
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                EventInfoFragment()
            }
            1 -> {
                EventTrucksFragment()
            }
            else -> {
                EventInfoFragment()
            }
        }
    }


}