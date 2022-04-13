package com.example.youfood

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.runBlocking
import org.w3c.dom.Text
import java.io.File


class TruckScreen : AppCompatActivity(), OnMapReadyCallback {

    private var lon : Double = 0.0
    private var lat : Double = 0.0
    private lateinit var infoArray : Array<String>

    private lateinit var reviewBodyArray : Array<String>
    private lateinit var reviewAuthorArray: Array<String>
    private lateinit var reviewDateArray: Array<String>

    private lateinit var reviewArrayList : ArrayList<Review>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_truck_screen)

        val truckName = intent.getStringExtra("TruckName")
        val backButton = findViewById<ImageButton>(R.id.truckBackButton)

        val file = File(filesDir, "records.txt").readLines()
        val email = file[0]

        Log.i("Email", email)



        runBlocking {
            val (_, _, result) = Fuel.get("http://foodtruckfindermi.com/get-truck-info?name=${truckName}").awaitStringResponseResult()
            result.fold(
                {data ->
                    var answer = data.split("$")
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


        val reviewList = findViewById<ListView>(R.id.reviewList)
        val submitReviewButton = findViewById<Button>(R.id.submitReviewButton)
        val bodyReviewTextEdit = findViewById<EditText>(R.id.bodyTextEdit)
        val truckemail = infoArray[1]



        runBlocking {
            val (request, response, result) = Fuel.get("http://foodtruckfindermi.com/review-query?truck=${truckemail}")
                .awaitStringResponseResult()

            result.fold(
                { data ->
                    var reviewArray = data.split("^")
                    Log.i("Arrays", reviewArray[0].toString())

                    reviewAuthorArray = reviewArray[0].split("`").drop(1).toTypedArray()

                    reviewBodyArray = reviewArray[1].split("`").drop(1).toTypedArray()

                    reviewDateArray = reviewArray[2].split("`").drop(1).toTypedArray()

                    reviewArrayList = ArrayList()

                    for(i in reviewAuthorArray.indices){

                        val review = Review(reviewAuthorArray[i], reviewBodyArray[i], reviewDateArray[i])
                        reviewArrayList.add(review)
                    }

                    reviewList.adapter = ReviewAdapter(this@TruckScreen, reviewArrayList)

                    var totalHeight = 0
                    for (i in 0 until reviewList.adapter.getCount()) {
                        val listItem: View = reviewList.adapter.getView(i, null, reviewList)
                        listItem.measure(0, 0)
                        totalHeight += listItem.getMeasuredHeight() + listItem.getMeasuredHeightAndState() / 2
                    }
                    val params: ViewGroup.LayoutParams = reviewList.getLayoutParams()
                    params.height = totalHeight + reviewList.getDividerHeight() * (reviewList.adapter.getCount() - 1)
                    reviewList.setLayoutParams(params)
                    reviewList.requestLayout()

                },
                { error -> Log.e("http", "$error") })
        }


        submitReviewButton.setOnClickListener {
            runBlocking {
                val (request, response, result) = Fuel.post(
                    "http://foodtruckfindermi.com/create-review",
                    listOf("author" to email, "body" to bodyReviewTextEdit.text, "truck" to truckemail)
                ).awaitStringResponseResult()
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
        val website = info[5]
        val isOpen = info[6]
        lon = info[5].toDouble()
        lat = info[6].toDouble()

        val nameLabel = findViewById<TextView>(R.id.truckName)
        val openLabel = findViewById<TextView>(R.id.openLabel)
        val cityLabel = findViewById<TextView>(R.id.cityLabel)
        val foodLabel = findViewById<TextView>(R.id.foodLabel)
        val emailLabel = findViewById<TextView>(R.id.emailLabel)
        val websiteLabel = findViewById<TextView>(R.id.websiteLabel)
        val websiteIcon = findViewById<ImageView>(R.id.websiteIcon)

        nameLabel.text = name
        when (isOpen) {
            "0" ->  openLabel.text = getString(R.string.closed_hint)
            "1" ->  openLabel.text = getString(R.string.open_hint)
        }
        cityLabel.text = city
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