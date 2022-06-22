package com.example.youfood.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.youfood.Fragments.EventInfoFragment
import com.example.youfood.Fragments.EventTrucksFragment

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