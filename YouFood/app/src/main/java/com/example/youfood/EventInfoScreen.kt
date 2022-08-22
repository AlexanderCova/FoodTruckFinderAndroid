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
import org.json.JSONObject
import java.io.File

class EventInfo (name : String, goingAmount : Int, goingList : String) {}





class EventInfoScreen : AppCompatActivity() {

    var interested : Boolean = false
    lateinit var name : String
    lateinit var email : String


    private lateinit var goingArray : MutableList<String>
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

        loadScreen()




        goingButton.setOnClickListener {
            if (interested) {
                goingArray.remove(email)
                goingButton.text = getString(R.string.uninterested)
                goingLabel.text = goingArray.count().toString()
                var updatedList = ""

                for (i in 0 until(goingArray.count())) {
                    updatedList += "`" + goingArray[i]
                }

                runBlocking{
                    val (_, _, _) = Fuel.post("http://foodtruckfindermi.com/update-going", listOf("event" to name, "updated-list" to updatedList)).awaitStringResponseResult()
                }
            } else {
                goingArray.add(email)
                goingButton.text = getString(R.string.interested)
                goingLabel.text = goingArray.count().toString()

                var updatedList = ""

                for (i in 0 until(goingArray.count())) {
                    updatedList += "`" + goingArray[i]
                }

                runBlocking {
                    val (_, _, _) = Fuel.post("http://foodtruckfindermi.com/update-going", listOf("event" to name, "updated-list" to updatedList)).awaitStringResponseResult()
                    loadScreen()
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

    fun loadScreen() {
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
                    val jsonString = """
                        {
                            "Event": $data
                        }
                    """.trimIndent()


                    val eventJsonObject = JSONObject(jsonString)
                    val eventObject = eventJsonObject.getJSONArray("Event")


                    val goingArrayString = eventObject.getJSONObject(0).getString("going")
                    goingArray = goingArrayString.split("`").drop(1).toMutableList()

                    interested = email in goingArray

                    if (interested) {
                        goingButton.text = getString(R.string.uninterested)
                    } else {
                        goingButton.text = getString(R.string.interested)
                    }

                    nameLabel.text = eventObject.getJSONObject(0).getString("name")
                    goingLabel.text = goingArray.count().toString()




                    //TODO figure out what info is where and add it into the screen



                },
                { error -> Log.e("http", "$error") }
            )
        }
    }



}



