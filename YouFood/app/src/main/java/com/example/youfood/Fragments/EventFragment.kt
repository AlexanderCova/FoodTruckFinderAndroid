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
import org.json.JSONObject
import kotlin.collections.ArrayList


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
                    val jsonString = """
                        {
                            "Events": $data
                        }
                    """.trimIndent()

                    val searchView = eventSearchView
                    val eventArrayList = ArrayList<Event>()

                    val eventJsonObject = JSONObject(jsonString)
                    val eventObject = eventJsonObject.getJSONArray("Events")

                    for (i in 0 until(eventObject.length())) {
                        val event = Event(
                            eventObject.getJSONObject(i).getString("name"),
                            eventObject.getJSONObject(i).getString("date"),
                            eventObject.getJSONObject(i).getString("desc")
                        )

                        eventArrayList.add(event)
                    }
                    eventList.adapter = EventAdapter(requireActivity(), eventArrayList)
                    eventList.setOnItemClickListener { _, _, position, _ ->

                        val intent = Intent(requireActivity(), EventInfoScreen::class.java)
                        Log.i("INFO", eventArrayList[position].name)
                        intent.putExtra("name", eventArrayList[position].name)
                        startActivity(intent)

                    }

                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {

                            val newEventList = ArrayList<Event>()
                            searchView.clearFocus()
                            for (i in eventArrayList.indices) {
                                if (eventArrayList[i].name.contains(query.toString())) {
                                    newEventList.add(eventArrayList[i])
                                    eventList.adapter = EventAdapter(requireActivity(), newEventList)
                                }
                            }
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            val newEventList = ArrayList<Event>()

                            for (i in eventArrayList.indices) {
                                if (eventArrayList[i].name.contains(newText.toString())) {
                                    newEventList.add(eventArrayList[i])
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