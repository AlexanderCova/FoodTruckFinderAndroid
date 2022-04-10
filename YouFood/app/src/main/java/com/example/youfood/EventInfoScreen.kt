package com.example.youfood

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.database.getIntOrNull
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.coroutines.runBlocking

class EventInfoScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info_screen)

        val name = intent.getStringExtra("name")

        val goingLabel = findViewById<TextView>(R.id.goingAmount)
        val nameLabel = findViewById<TextView>(R.id.eventName)
        val goingButton = findViewById<Button>(R.id.goingButton)

        val mainDB = DBHelper(this, null)
        val cursor = mainDB.getInterest(name!!)
        val interest = cursor!!.getIntOrNull(0)

        if (interest != null) {
            goingButton.text = "Uninterested"
        }


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


                    nameLabel.text = name
                    goingLabel.text = going



                },
                { error -> Log.e("http", "$error")}
            )
        }

        goingButton.setOnClickListener {
            val db = DBHelper(this, null)

            val text = db.setIntrest(name!!)

            goingButton.text = text


        }











    }
}