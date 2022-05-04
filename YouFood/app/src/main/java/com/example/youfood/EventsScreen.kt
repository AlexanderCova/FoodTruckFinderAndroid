package com.example.youfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.coroutines.runBlocking

class EventsScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_screen)

        val eventList = findViewById<ListView>(R.id.eventList)
        val searchTabButton = findViewById<ImageButton>(R.id.searchTabButton)
        val accountTabButton = findViewById<ImageButton>(R.id.accountTabButton)

        searchTabButton.setOnClickListener {
            val intent = Intent(this, UserScreen::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        accountTabButton.setOnClickListener {
            val intent = Intent(this, AccountScreen::class.java)
            startActivity(intent)

        }


        runBlocking {
            val (_, _, result) = Fuel.get("http://foodtruckfindermi.com/event-query")
                .awaitStringResponseResult()

            result.fold(
                { data ->
                    val eventArray = data.split("^")
                    val eventNameArray = eventArray[0].split("`").drop(1)
                    val eventDescArray = eventArray[1].split("`").drop(1)
                    val eventDateArray = eventArray[2].split("`").drop(1)
                    val searchView = findViewById<SearchView>(R.id.searchView)

                    val eventArrayList = ArrayList<Event>()

                    for (i in eventNameArray.indices) {
                        val event = Event(eventNameArray[i], eventDescArray[i], eventDateArray[i])
                        eventArrayList.add(event)
                    }
                    eventList.adapter = EventAdapter(this@EventsScreen, eventArrayList)
                    eventList.setOnItemClickListener { _, _, position, _ ->

                        val intent = Intent(this@EventsScreen, EventInfoScreen::class.java)
                        intent.putExtra("name", eventNameArray[position])
                        startActivity(intent)

                    }

                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {

                            val newEventList = ArrayList<Event>()
                            searchView.clearFocus()
                            for (i in eventNameArray.indices) {
                                if (eventNameArray[i].contains(query.toString())) {
                                    newEventList.add(Event(eventNameArray[i], eventDescArray[i], eventDateArray[i]))
                                    eventList.adapter = EventAdapter(this@EventsScreen, newEventList)
                                }
                            }
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            val newEventList = ArrayList<Event>()

                            for (i in eventNameArray.indices) {
                                if (eventNameArray[i].contains(newText.toString())) {
                                    newEventList.add(Event(eventNameArray[i], eventDescArray[i], eventDateArray[i]))
                                    eventList.adapter = EventAdapter(this@EventsScreen, newEventList)
                                }
                            }
                            return false
                        }
                    })


                },
                { error -> Log.e("http", error.toString())}
            )
        }
    }
}