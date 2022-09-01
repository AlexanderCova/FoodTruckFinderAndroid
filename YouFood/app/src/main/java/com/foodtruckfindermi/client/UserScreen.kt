package com.foodtruckfindermi.client

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.foodtruckfindermi.client.Adapter.PagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_user_screen.*

class UserScreen : AppCompatActivity() {

    private lateinit var mViewPager: ViewPager
    private lateinit var searchBtn : ImageButton
    private lateinit var eventBtn : ImageButton
    private lateinit var mPagerAdapter: PagerAdapter

    private lateinit var mPagerViewAdapter: PagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_screen)

        mViewPager = findViewById(R.id.mViewPager)
        searchBtn = findViewById(R.id.searchBtn)
        eventBtn = findViewById(R.id.eventBtn)

        searchBtn.setOnClickListener {
            mViewPager.currentItem = 0
        }

        eventBtn.setOnClickListener {
            mViewPager.currentItem = 1
        }


        mPagerViewAdapter = PagerAdapter(supportFragmentManager)
        mViewPager.adapter = mPagerViewAdapter
        mViewPager.offscreenPageLimit = 2


        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                changeTabs(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })




        mViewPager.currentItem = 0
        searchBtn.setBackgroundResource(R.drawable.selected_tab_overlay)





    }

    private fun changeTabs(position: Int) {


        if (position == 0) {
            searchBtn.setBackgroundResource(R.drawable.selected_tab_overlay)
            eventBtn.setBackgroundResource(0)


        }
        if (position == 1) {
            searchBtn.setBackgroundResource(0)
            eventBtn.setBackgroundResource(R.drawable.selected_tab_overlay)

        }


    }
}