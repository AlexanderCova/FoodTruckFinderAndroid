package com.example.youfood

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.youfood.Adapter.PagerAdapter
import com.example.youfood.Adapter.ReviewAdapter
import com.example.youfood.Adapter.TruckPagerAdapter
import com.example.youfood.DataClasses.Review
import com.example.youfood.databinding.ActivityTruckScreenBinding
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_truck_screen.*
import kotlinx.android.synthetic.main.fragment_truck_reviews.*
import kotlinx.coroutines.runBlocking
import java.io.File


class TruckScreen : AppCompatActivity(), OnMapReadyCallback {

    var lon : Double = 0.0
    var lat : Double = 0.0
    private lateinit var infoArray : Array<String>

    private lateinit var reviewBodyArray : Array<String>
    private lateinit var reviewAuthorArray: Array<String>
    private lateinit var reviewDateArray: Array<String>
    private lateinit var reviewRatingArray: Array<String>

    private lateinit var reviewArrayList : ArrayList<Review>

    private lateinit var binding: ActivityTruckScreenBinding

    lateinit var truckemail : String
    lateinit var truckName : String



    private lateinit var mPagerAdapter: TruckPagerAdapter
    private lateinit var mPagerViewAdapter: TruckPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTruckScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        truckName = intent.getStringExtra("TruckName") as String
        val flag = intent.getStringExtra("flag") as String
        val backButton = binding.truckBackButton

        val file = File(filesDir, "records.txt").readLines()
        val email = file[0]

        val mViewPager = binding.truckViewPager
        val infoTabButton = binding.infoTabButton
        val mapTabButton = binding.mapTabButton
        val reviewsTabButton = binding.reviewsTabButton




        runBlocking {
            val (_, _, result) = Fuel.get("http://foodtruckfindermi.com/get-truck-info?name=${truckName}").awaitStringResponseResult()
            result.fold(
                {data ->
                    var answer = data.split("`")
                    answer = answer.drop(1)
                    infoArray = answer.toTypedArray()

                    loadScreen(infoArray)
                },
                { error ->
                    Log.e("http", "${error.exception}")
                }
            )
        }
        var intent = Intent(this, UserScreen::class.java)

        backButton.setOnClickListener {
            when (flag) {
                "user" -> {intent = Intent(this, UserScreen::class.java)}
                "event" -> {
                    val eventName = intent.getStringExtra("event")
                    intent = Intent(this, EventInfoScreen::class.java)
                    intent.putExtra("name", eventName)
                }
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        truckemail = infoArray[2]



        infoTabButton.setOnClickListener {
            mViewPager.currentItem = 0
        }

        mapTabButton.setOnClickListener {
            mViewPager.currentItem = 1
        }

        reviewsTabButton.setOnClickListener {
            mViewPager.currentItem = 2
        }

        mPagerViewAdapter = TruckPagerAdapter(supportFragmentManager)
        mViewPager.adapter = mPagerViewAdapter
        mViewPager.offscreenPageLimit = 3


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
        infoTabButton.setBackgroundColor(resources.getColor(R.color.gold))
    }

    private fun loadScreen(info : Array<String>) {
        val name = info[0]
        val profile = info[5]
        lon = info[8].toDouble()
        lat = info[9].toDouble()
        val rating = info[10]


        val nameLabel = binding.truckName
        val profileImg = binding.profilePic
        val ratingBar = binding.ratingBar

        nameLabel.text = name
        Log.i("rating", rating)

        if (rating != "" && rating != "None") {
            ratingBar.rating = rating.toFloat()
            Log.i("rating", rating)
        } else {
            ratingBar.rating = 0.0F
        }






        val bytes = Base64.decode(profile, Base64.DEFAULT)
        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        profileImg.setImageBitmap(bmp)






    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(lat, lon)))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lon)))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 15F))

    }

    private fun changeTabs(position: Int) {
        val infoTabButton = binding.infoTabButton
        val mapTabButton = binding.mapTabButton
        val reviewsTabButton = binding.reviewsTabButton


        if (position == 0) {
            infoTabButton.setBackgroundColor(resources.getColor(R.color.gold))
            mapTabButton.setBackgroundColor(resources.getColor(R.color.gray))
            reviewsTabButton.setBackgroundColor(resources.getColor(R.color.gray))

        }
        if (position == 1) {
            infoTabButton.setBackgroundColor(resources.getColor(R.color.gray))
            mapTabButton.setBackgroundColor(resources.getColor(R.color.gold))
            reviewsTabButton.setBackgroundColor(resources.getColor(R.color.gray))
        }
        if (position == 2) {
            infoTabButton.setBackgroundColor(resources.getColor(R.color.gray))
            mapTabButton.setBackgroundColor(resources.getColor(R.color.gray))
            reviewsTabButton.setBackgroundColor(resources.getColor(R.color.gold))
        }


    }
}