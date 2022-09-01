package com.foodtruckfindermi.client.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.foodtruckfindermi.client.Fragments.EventFragment
import com.foodtruckfindermi.client.Fragments.TrucksFragment

internal class PagerAdapter(fm: FragmentManager?) :
        FragmentPagerAdapter(fm!!){

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> {
                TrucksFragment()
            }
            1 -> {
                EventFragment()
            }

            else -> {
                TrucksFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

        }