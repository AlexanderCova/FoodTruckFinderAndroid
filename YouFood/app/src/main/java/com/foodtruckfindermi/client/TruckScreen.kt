package com.foodtruckfindermi.client

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.foodtruckfindermi.client.Adapter.TruckPagerAdapter
import com.foodtruckfindermi.client.databinding.ActivityTruckScreenBinding
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.google.android.gms.maps.*
import kotlinx.android.synthetic.main.activity_truck_screen.*
import kotlinx.android.synthetic.main.fragment_truck_reviews.*
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.io.File

class TruckInfo (
    var name : String,
    var city : String,
    var email : String,
    var foodtype : String,
    var profile : String,
    var website : String,
    var isopen : String,
    var lon : String,
    var lat : String,
    var rating : String) {}




class TruckScreen : AppCompatActivity() {

    var lon : Double = 0.0
    var lat : Double = 0.0

    private lateinit var truckinfo : TruckInfo

    private lateinit var binding: ActivityTruckScreenBinding

    lateinit var truckemail : String
    lateinit var truckName : String
    lateinit var isOpen : String



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
                    val truckJsonString = """
                        {
                            "Truck": $data
                        }
                    """.trimIndent()

                    val truckJsonObject = JSONObject(truckJsonString)
                    val truckObject = truckJsonObject.getJSONArray("Truck")

                    truckinfo = TruckInfo(
                        truckObject.getJSONObject(0).getString("truckname"),
                        truckObject.getJSONObject(0).getString("city"),
                        truckObject.getJSONObject(0).getString("email"),
                        truckObject.getJSONObject(0).getString("foodtype"),
                        truckObject.getJSONObject(0).getString("profile"),
                        truckObject.getJSONObject(0).getString("website"),
                        truckObject.getJSONObject(0).getString("isopen"),
                        truckObject.getJSONObject(0).getString("lon"),
                        truckObject.getJSONObject(0).getString("lat"),
                        truckObject.getJSONObject(0).getString("rating"))

                    loadScreen(truckinfo)
                },
                { error ->
                    Log.e("http", "${error.exception}")
                }
            )
        }

        backButton.setOnClickListener {
            onBackPressed()
        }

        truckemail = truckinfo.email



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

    private fun loadScreen(truck : TruckInfo) {
        val name = truck.name
        val profile = truck.profile
        lon = truck.lon.toDouble()
        lat = truck.lat.toDouble()
        val rating = truck.rating
        isOpen = truck.isopen


        val nameLabel = binding.truckName
        val profileImg = binding.profilePic
        val ratingBar = binding.ratingBar

        nameLabel.text = name
        Log.i("rating", rating)

        if (rating != "" && rating != "null") {
            ratingBar.rating = rating.toFloat()
            Log.i("rating", rating)
        } else {
            ratingBar.rating = 0.0F
        }

        val bytes = Base64.decode(profile, Base64.DEFAULT)
        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        profileImg.setImageBitmap(bmp)

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