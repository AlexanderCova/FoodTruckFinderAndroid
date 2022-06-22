package com.example.youfood.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.youfood.Adapter.TruckAdapter
import com.example.youfood.DataClasses.Truck
import com.example.youfood.EventInfoScreen
import com.example.youfood.R
import com.example.youfood.TruckScreen
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.android.synthetic.main.fragment_trucks.*
import kotlinx.coroutines.runBlocking

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
            val (_, _, result) = Fuel.get("http://foodtruckfindermi.com/event-truck-query", listOf("name" to name))
                .awaitStringResponseResult()

            result.fold(
                { data ->
                    val truckArray = data.split("^")
                    val truckNameArray = truckArray[0].split("`").drop(1)
                    val truckProfileArray = truckArray[1].split("`").drop(1)
                    val isOpenArray = truckArray[2].split("`").drop(1)
                    val ratingArray = truckArray[3].split("`").drop(1)
                    val foodTypeArray = truckArray[4].split("`").drop(1)

                    val truckArrayList = ArrayList<Truck>()

                    var i = 0
                    repeat(truckNameArray.count()) {

                        val truck = Truck(
                            truckNameArray[i],
                            truckProfileArray[i],
                            isOpenArray[i],
                            ratingArray[i],
                            foodTypeArray[i]
                        )
                        truckArrayList.add(truck)
                        i += 1
                    }
                    truckList.adapter = TruckAdapter(requireActivity(), truckArrayList)
                    truckList.setOnItemClickListener { _, _, position, _ ->

                        val intent = Intent(requireActivity(), TruckScreen::class.java)
                        intent.putExtra("TruckName", truckNameArray[position])
                        intent.putExtra("flag", "event")
                        startActivity(intent)
                    }

                },
                { error -> Log.e("http", "$error")}
            )
        }

    }
}