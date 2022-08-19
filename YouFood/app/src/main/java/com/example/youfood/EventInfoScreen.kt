package com.example.youfood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.youfood.Adapter.EventPagerAdapter
import com.example.youfood.Adapter.TruckPagerAdapter
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.coroutines.runBlocking
import java.io.File

class EventInfoScreen : AppCompatActivity() {

    var interested : Boolean = false
    lateinit var name : String
    lateinit var email : String

    private lateinit var mPagerAdapter: EventPagerAdapter
    private lateinit var mPagerViewAdapter: EventPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info_screen)

        val mViewPager = findViewById<ViewPager>(R.id.eventViewPager)

        name = intent.getStringExtra("name")!!
        Log.i("INFO", name)

        val goingLabel = findViewById<TextView>(R.id.goingAmount)
        val nameLabel = findViewById<TextView>(R.id.eventName)
        val goingButton = findViewById<Button>(R.id.goingButton)
        val infoButton = findViewById<Button>(R.id.eventInfoTab)
        val trucksButton = findViewById<Button>(R.id.eventTrucksTab)

        val accountFile = File(filesDir, "records.txt").readLines()
        email = accountFile[0]

        load_screen(name, email)




        goingButton.setOnClickListener {
            if (interested) {
                Log.i("Unattending", "This is being clicked")
                runBlocking {
                    val (_,_, result) = Fuel.post("http://foodtruckfindermi.com/unattending-event", listOf("name" to name, "account" to email)).awaitStringResponseResult()

                    result.fold(
                        { data ->
                            load_screen(name, email)
                            interested = false
                            goingButton.text = getString(R.string.interested)
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

        infoButton.setOnClickListener {
            mViewPager.currentItem = 0
        }

        trucksButton.setOnClickListener {
            mViewPager.currentItem = 1
        }

        mPagerViewAdapter = EventPagerAdapter(supportFragmentManager)
        mViewPager.adapter = mPagerViewAdapter
        mViewPager.offscreenPageLimit = 2



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
                    var eventInfoList = data.split("`")
                    eventInfoList = eventInfoList.drop(1)

                    //TODO figure out what info is where and add it into the screen
                    val eventName = eventInfoList[0]
                    val desc = eventInfoList[1]
                    val date = eventInfoList[2]
                    val city = eventInfoList[3]
                    val goingAmount = eventInfoList[4]
                    val isGoing = eventInfoList[5]



                    nameLabel.text = name
                    goingLabel.text = goingAmount

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



