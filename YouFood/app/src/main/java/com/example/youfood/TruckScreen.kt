package com.example.youfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class TruckScreen : AppCompatActivity(), OnMapReadyCallback {

    private var lon : Double = 0.0
    private var lat : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_truck_screen)

        val truckName = intent.getStringExtra("TruckName")

        Fuel.get("http://foodtruckfindermi.com/get-truck-info?name=${truckName}")
            .response { _request, _response, result ->
                val (bytes) = result
                Log.i("request", "request sent")
                if (bytes != null) {
                    var response = String(bytes).split("$")
                    response = response.drop(1)
                    Log.i("request", response.toString())

                    var infoArray = response.toTypedArray()

                    load_screen(infoArray)

                }

            }

        val map = getSupportFragmentManager().findFragmentById(R.id.map) as SupportMapFragment
        map.getMapAsync(this)
    }

    private fun load_screen(info : Array<String>) {
        val name = info[0]
        val email = info[1]
        val food = info[3]
        val isOpen = info[4]
        lon = info[5].toDouble()
        lat = info[6].toDouble()


    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(lat, lon))
        )
    }
}