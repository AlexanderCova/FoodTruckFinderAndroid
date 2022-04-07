package com.example.youfood

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.coroutines.runBlocking

class EventInfoScreen : AppCompatActivity() {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info_screen)

        val name = intent.getStringExtra("name")

        val goingLabel = findViewById<TextView>(R.id.goingAmount)
        val maybeLabel = findViewById<TextView>(R.id.maybeAmount)
        val nameLabel = findViewById<TextView>(R.id.eventName)
        val goingSwitch = findViewById<Switch>(R.id.goingSwitch)
        val maybeSwitch = findViewById<Switch>(R.id.maybeSwitch)

        runBlocking {
            val (_, _, result) = Fuel.post("http://foodtruckfindermi.com/get-event-info", listOf("name" to name))
                .awaitStringResponseResult()

            result.fold(
                { data ->
                    val eventInfoList = data.split("`")

                    //TODO figure out what info is where and add it into the screen
                    val eventName = eventInfoList[0]
                    val desc = eventInfoList[1]
                    val date = eventInfoList[2]
                    val city = eventInfoList[3]
                    val going = eventInfoList[5]
                    val maybe = eventInfoList[6]

                    nameLabel.text = name
                    goingLabel.text = going
                    maybeLabel.text = maybe


                },
                { error -> Log.e("http", "$error")}
            )
        }


        goingSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if (b == true) {
                maybeSwitch.isChecked = false
                val db = DBHelper(this, null)

                if (name != null) {
                    db.updateInterest(name, 2)
                }
            } else {
                val db = DBHelper(this, null)

                if (name != null) {
                    db.removeInterest(name)
                }
            }
        }

        maybeSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if (b == true) {
                goingSwitch.isChecked = false
                val db = DBHelper(this, null)

                if (name != null) {
                    db.updateInterest(name, 1)
                }
            } else {
                val db = DBHelper(this, null)

                if (name != null) {
                    db.removeInterest(name)
                }
            }
        }







    }
}