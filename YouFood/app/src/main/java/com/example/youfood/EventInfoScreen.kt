package com.example.youfood

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.database.getIntOrNull
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.coroutines.runBlocking
import java.io.File

class EventInfoScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info_screen)

        val name = intent.getStringExtra("name")

        val goingLabel = findViewById<TextView>(R.id.goingAmount)
        val nameLabel = findViewById<TextView>(R.id.eventName)
        val goingButton = findViewById<Button>(R.id.goingButton)

        val accountFile = File(filesDir, "records.txt").readLines()
        val email = accountFile[0]

        val file = File(filesDir, "events.txt")
        if (file.exists()) {
            val eventList = file.readLines().toMutableList()
            var eventIndex = eventList.indexOf(name)

            if (eventIndex != -1) {
                goingButton.text = getString(R.string.uninterested)
            } else {
                goingButton.text = getString(R.string.interested)
            }

        }




        runBlocking {
            val (_, _, result) = Fuel.get("http://foodtruckfindermi.com/get-event-info", listOf("name" to name))
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
            val file = File(filesDir,"events.txt")

            if (file.exists()) {
                val eventList = file.readLines().toMutableList()
                var eventIndex = eventList.indexOf(name)

                if (eventIndex != -1) {
                    eventIndex += 1

                    if (eventList[eventIndex] == "0") {
                        runBlocking {
                            val (_, _, result) = Fuel.post("http://foodtruckfindermi.com/attending-event", listOf("name" to name, "account" to email))
                                .awaitStringResponseResult()

                            eventList[eventIndex] = "1"
                            for(i in eventList) {
                                file.writeText(i + "\n")
                            }
                        }
                    } else {
                        runBlocking {
                            val (_, _, result) = Fuel.post("http://foodtruckfindermi.com/unattending-event", listOf("name" to name))
                                .awaitStringResponseResult()

                            eventList.removeAt(eventIndex)
                            eventList.removeAt(eventIndex - 1)
                            for (i in eventList){
                                file.writeText(i + "\n")
                            }
                        }
                    }
                } else {
                    file.appendText(name + "\n" + "1")
                }

            } else {
                runBlocking {
                    file.createNewFile()

                    val (_, _, result) = Fuel.post(
                        "http://foodtruckfindermi.com/attending-event",
                        listOf("name" to name, "account" to email)
                    ).awaitStringResponseResult()

                    val record = name + "\n" + 1
                    file.appendText(record)
                }
            }
        }



    }
}