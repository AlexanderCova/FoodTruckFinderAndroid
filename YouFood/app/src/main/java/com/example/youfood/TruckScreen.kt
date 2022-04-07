package com.example.youfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.runBlocking

class TruckScreen : AppCompatActivity(), OnMapReadyCallback {

    private var lon : Double = 0.0
    private var lat : Double = 0.0
    private lateinit var infoArray : Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_truck_screen)

        val truckName = intent.getStringExtra("TruckName")
        val backButton = findViewById<ImageButton>(R.id.truckBackButton)
        val reviewButton = findViewById<Button>(R.id.reviewsButton)


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

        reviewButton.setOnClickListener {
            val intent = Intent(this, ReviewScreen::class.java)
            intent.putExtra("truckemail", infoArray[1])
            intent.putExtra("TruckName", truckName)
            startActivity(intent)
        }




        val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        map.getMapAsync(this)
    }

    private fun loadScreen(info : Array<String>) {
        val name = info[0]
        val email = info[1]
        val food = info[3]
        val isOpen = info[4]
        lon = info[5].toDouble()
        lat = info[6].toDouble()

        val nameLabel = findViewById<TextView>(R.id.truckName)
        val openLabel = findViewById<TextView>(R.id.openLabel)
        val foodLabel = findViewById<TextView>(R.id.foodLabel)
        val emailLabel = findViewById<TextView>(R.id.emailLabel)

        nameLabel.text = name
        when (isOpen) {
            "0" ->  openLabel.text = getString(R.string.closed_hint)
            "1" ->  openLabel.text = getString(R.string.open_hint)
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