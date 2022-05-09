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
import com.example.youfood.databinding.ActivityTruckScreenBinding
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_truck_screen.*
import kotlinx.coroutines.runBlocking
import java.io.File


class TruckScreen : AppCompatActivity(), OnMapReadyCallback {

    private var lon : Double = 0.0
    private var lat : Double = 0.0
    private lateinit var infoArray : Array<String>

    private lateinit var reviewBodyArray : Array<String>
    private lateinit var reviewAuthorArray: Array<String>
    private lateinit var reviewDateArray: Array<String>
    private lateinit var reviewRatingArray: Array<String>

    private lateinit var reviewArrayList : ArrayList<Review>

    private lateinit var binding: ActivityTruckScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTruckScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val truckName = intent.getStringExtra("TruckName")
        val backButton = binding.truckBackButton

        val file = File(filesDir, "records.txt").readLines()
        val email = file[0]

        Log.i("Email", email)



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

        backButton.setOnClickListener {
            val intent = Intent(this, UserScreen::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        val truckemail = infoArray[2]
        val reviewList = binding.reviewList
        val submitReviewButton = binding.submitReviewButton
        val bodyReviewTextEdit = binding.bodyTextEdit
        val ratingBar = binding.reviewRatingBar





        runBlocking {
            val (_, _, result) = Fuel.get("http://foodtruckfindermi.com/review-query?truck=${truckemail}")
                .awaitStringResponseResult()

            result.fold(
                { data ->
                    val reviewArray = data.split("^")


                    reviewAuthorArray = reviewArray[0].split("`").drop(1).toTypedArray()

                    reviewBodyArray = reviewArray[1].split("`").drop(1).toTypedArray()

                    reviewDateArray = reviewArray[2].split("`").drop(1).toTypedArray()

                    reviewRatingArray = reviewArray[3].split("`").drop(1).toTypedArray()

                    reviewArrayList = ArrayList()

                    for(i in reviewAuthorArray.indices){

                        val review = Review(reviewAuthorArray[i], reviewBodyArray[i], reviewDateArray[i], reviewRatingArray[i].toFloat())
                        reviewArrayList.add(review)
                    }

                    reviewList.adapter = ReviewAdapter(this@TruckScreen, reviewArrayList)

                    var totalHeight = 0
                    for (i in 0 until reviewList.adapter.count) {
                        val listItem: View = reviewList.adapter.getView(i, null, reviewList)
                        listItem.measure(0, 0)
                        totalHeight += listItem.measuredHeight + listItem.measuredHeightAndState / 2
                    }
                    val params: ViewGroup.LayoutParams = reviewList.layoutParams
                    params.height = totalHeight + reviewList.dividerHeight * (reviewList.adapter.count - 1)
                    reviewList.layoutParams = params
                    reviewList.requestLayout()

                },
                { error -> Log.e("http", "$error") })
        }


        submitReviewButton.setOnClickListener {
            runBlocking {
                val (_, _, result) = Fuel.post(
                    "http://foodtruckfindermi.com/create-review",
                    listOf("author" to email, "body" to bodyReviewTextEdit.text, "truck" to truckemail, "rating" to ratingBar.rating)
                ).awaitStringResponseResult()

                result.fold(
                    {data ->
                        if (data == "true") {
                            bodyReviewTextEdit.text.clear()
                            ratingBar.rating = 0F
                        }
                    },
                    {error -> Log.e("http", "$error")}
                )

            }
        }




        val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        map.getMapAsync(this)
    }

    private fun loadScreen(info : Array<String>) {
        val name = info[0]
        val city = info[1]
        val email = info[2]
        val food = info[4]
        val profile = info[5]
        val website = info[6]
        val isOpen = info[7]
        lon = info[8].toDouble()
        lat = info[9].toDouble()
        val rating = info[10]


        val nameLabel = binding.truckName
        val openLabel = binding.openLabel
        val cityLabel = binding.cityLabel
        val cityIcon = binding.cityIcon
        val foodLabel = binding.foodLabel
        val emailLabel = binding.emailLabel
        val profileImg = binding.profilePic
        val websiteLabel = binding.websiteLabel
        val websiteIcon = binding.websiteIcon
        val ratingBar = binding.ratingBar

        nameLabel.text = name
        Log.i("rating", rating)

        if (rating != "" && rating != "None") {
            ratingBar.rating = rating.toFloat()
            Log.i("rating", rating)
        } else {
            ratingBar.rating = 0.0F
        }


        when (isOpen) {
            "0" ->  openLabel.text = getString(R.string.closed_hint)
            "1" ->  openLabel.text = getString(R.string.open_hint)
        }

        cityLabel.text = city

        val bytes = Base64.decode(profile, Base64.DEFAULT)
        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        profileImg.setImageBitmap(bmp)

        if (city == ""){
            cityLabel.visibility = View.GONE
            cityIcon.visibility = View.GONE
        } else {
            cityLabel.text = city
            cityLabel.visibility = View.VISIBLE
            cityIcon.visibility = View.VISIBLE
        }


        if (website == "") {
            websiteLabel.visibility = View.GONE
            websiteIcon.visibility = View.GONE
        } else {
            websiteLabel.text = "<a href='${website}'>Our Website</a>"
            websiteLabel.movementMethod = LinkMovementMethod.getInstance()
            websiteLabel.visibility = View.VISIBLE
            websiteIcon.visibility = View.VISIBLE
        }

        foodLabel.text = food
        emailLabel.text = email

    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(lat, lon)))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lon)))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 15F))

    }
}