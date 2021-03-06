package com.example.youfood.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import com.example.youfood.DataClasses.Event
import com.example.youfood.Adapter.EventAdapter
import com.example.youfood.EventInfoScreen
import com.example.youfood.R
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.android.synthetic.main.fragment_event.*
import kotlinx.coroutines.runBlocking


class EventFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("FRAGMENT", "Loaded event fragment")

        runBlocking {
            val (_, _, result) = Fuel.get("http://foodtruckfindermi.com/event-query")
                .awaitStringResponseResult()

            result.fold(
                { data ->
                    val eventArray = data.split("^")
                    val eventNameArray = eventArray[0].split("`").drop(1).toTypedArray()
                    val eventDescArray = eventArray[1].split("`").drop(1)
                    val eventDateArray = eventArray[2].split("`").drop(1)
                    val searchView = eventSearchView

                    val eventArrayList = ArrayList<Event>()

                    var i = 0

                    repeat (eventNameArray.count()) {

                        val event = Event(eventNameArray[i], eventDescArray[i], eventDateArray[i])
                        eventArrayList.add(event)
                        i += 1
                    }
                    eventList.adapter = EventAdapter(requireActivity(), eventArrayList)
                    eventList.setOnItemClickListener { _, _, position, _ ->

                        val intent = Intent(requireActivity(), EventInfoScreen::class.java)
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
                                    eventList.adapter = EventAdapter(requireActivity(), newEventList)
                                }
                            }
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            val newEventList = ArrayList<Event>()

                            for (i in eventNameArray.indices) {
                                if (eventNameArray[i].contains(newText.toString())) {
                                    newEventList.add(Event(eventNameArray[i], eventDescArray[i], eventDateArray[i]))
                                    eventList.adapter = EventAdapter(requireActivity(), newEventList)
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