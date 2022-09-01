package com.foodtruckfindermi.client.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foodtruckfindermi.client.Adapter.TruckAdapter
import com.foodtruckfindermi.client.DataClasses.Truck
import com.foodtruckfindermi.client.EventInfoScreen
import com.foodtruckfindermi.client.R
import com.foodtruckfindermi.client.TruckScreen
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.android.synthetic.main.fragment_event_trucks.*
import kotlinx.android.synthetic.main.fragment_trucks.*
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class EventTrucksFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_trucks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = (activity as EventInfoScreen).name


        runBlocking {
            val (_, _, result) = Fuel.get("http://foodtruckfindermi.com/get-event-info", listOf("name" to name))
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


                    val truckArrayString = eventObject.getJSONObject(0).getString("trucks")
                    val truckArray = truckArrayString.split("`").drop(1).toMutableList()

                    Log.i("INFO",truckArray.toString())





                    val truckArrayList = getTruckArray(truckArray)

                    Log.i("INFO",truckArrayList.toString())

                    eventTruckList.adapter = TruckAdapter(requireActivity(), truckArrayList)
                    eventTruckList .setOnItemClickListener { _, _, position, _ ->

                        val intent = Intent(requireActivity(), TruckScreen::class.java)
                        intent.putExtra("TruckName", truckArrayList[position].name)
                        intent.putExtra("flag", "event")
                        startActivity(intent)
                    }

                },
                { error -> Log.e("http", "$error")}
            )
        }

    }


    private fun getTruckArray(nameList : MutableList<String>): ArrayList<Truck> {
        var truckArrayList = ArrayList<Truck>()

        runBlocking {
            val (_, _, result) = Fuel.get("http://foodtruckfindermi.com/truck-query")
                .awaitStringResponseResult()

            result.fold(
                { data ->
                    val json_string = """
                        {
                            "Trucks": $data
                            
                        }
                    """.trimIndent()
                    val searchView = truckSearchView

                    truckArrayList = ArrayList<Truck>()

                    val truckJsonObject = JSONObject(json_string)
                    val truckObject = truckJsonObject.getJSONArray("Trucks")

                    for (i in 0 until (truckObject.length())) {
                        if (truckObject.getJSONObject(i).getString("truckname") in nameList) {

                            val truck = Truck(
                                truckObject.getJSONObject(i).getString("truckname"),
                                truckObject.getJSONObject(i).getString("profile"),
                                truckObject.getJSONObject(i).getString("isopen"),
                                truckObject.getJSONObject(i).getString("rating"),
                                truckObject.getJSONObject(i).getString("foodtype")
                            )

                            truckArrayList.add(truck)
                        }
                    }
                    return@fold truckArrayList
                },
                { error -> Log.e("http", "$error") }
            )
        }

        return truckArrayList

    }
}