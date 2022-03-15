package com.example.youfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.kittinunf.fuel.Fuel

class TruckScreen : AppCompatActivity() {
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

                }

            }
    }

    private fun load_screen(info : Array<String>) {
        val name = info[0]
        val email = info[1]
        val isOpen = info[4]
        val food = info[3]
    }
}