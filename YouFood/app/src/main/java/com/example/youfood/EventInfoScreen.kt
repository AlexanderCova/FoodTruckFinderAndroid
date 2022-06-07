package com.example.youfood

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.coroutines.runBlocking
import java.io.File

class EventInfoScreen : AppCompatActivity() {

    var interested : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info_screen)

        val name = intent.getStringExtra("name")

        val goingLabel = findViewById<TextView>(R.id.goingAmount)
        val nameLabel = findViewById<TextView>(R.id.eventName)
        val goingButton = findViewById<Button>(R.id.goingButton)

        val accountFile = File(filesDir, "records.txt").readLines()
        val email = accountFile[0]

        load_screen(name!!, email)




        goingButton.setOnClickListener {
            if (interested) {
                runBlocking {
                    val (_,_, result) = Fuel.post("http://foodtruckfinder.com/unattending-event", listOf("name" to name, "account" to email)).awaitStringResponseResult()

                    result.fold(
                        { data ->
                            load_screen(name, email)
                        },
                        { error -> Log.e("http", "$error")}
                    )
                }
            } else {
                runBlocking {
                    val (_,_, result) = Fuel.post("http://foodtruckfindermi.com/attending-event", listOf("name" to name, "account" to email)).awaitStringResponseResult()

                    result.fold(
                        { data ->
                            load_screen(name, email)
                        },
                        { error -> Log.e("http", "$error")}
                    )
                }
            }
        }



    }

    fun load_screen(name : String, email : String) {
        val goingLabel = findViewById<TextView>(R.id.goingAmount)
        val nameLabel = findViewById<TextView>(R.id.eventName)
        val goingButton = findViewById<Button>(R.id.goingButton)

        runBlocking {
            val (_, _, result) = Fuel.get(
                "http://foodtruckfindermi.com/get-event-info",
                listOf("name" to name, "account" to email)
            )
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
                    val isGoing = eventInfoList[6]


                    nameLabel.text = name
                    goingLabel.text = going

                    if (isGoing == "true") {
                        goingButton.text = getString(R.string.uninterested)
                        interested = true
                    } else {
                        goingButton.text = getString(R.string.interested)
                        interested = false
                    }


                },
                { error -> Log.e("http", "$error") }
            )
        }
    }
}



