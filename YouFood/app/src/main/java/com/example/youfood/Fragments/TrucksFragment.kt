package com.example.youfood.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import com.example.youfood.R
import com.example.youfood.Adapter.TruckAdapter
import com.example.youfood.TruckScreen
import com.example.youfood.DataClasses.Truck
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.android.synthetic.main.fragment_trucks.*
import kotlinx.coroutines.runBlocking


class TrucksFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trucks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearch()

    }

    private fun array(array: Array<String>): ArrayAdapter<String> {

        return ArrayAdapter(
            requireActivity(), android.R.layout.simple_list_item_1,
            array
        )
    }

    private fun setupSearch() {

        runBlocking {
            val (_, _, result) = Fuel.get("http://foodtruckfindermi.com/truck-query")
                .awaitStringResponseResult()

            result.fold(
                { data ->
                    val truckArray = data.split("^")
                    val truckNameArray = truckArray[0].split("`").drop(1)
                    val truckProfileArray = truckArray[1].split("`").drop(1)
                    val isOpenArray = truckArray[2].split("`").drop(1)
                    val ratingArray = truckArray[3].split("`").drop(1)
                    val foodTypeArray = truckArray[4].split("`").drop(1)
                    val searchView = truckSearchView

                    val truckArrayList = ArrayList<Truck>()

                    var i = 0
                    repeat (truckNameArray.count()) {

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
                        startActivity(intent)
                    }

                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {

                            val newTruckList = ArrayList<Truck>()
                            searchView.clearFocus()
                            for (i in truckNameArray.indices) {
                                if (truckNameArray[i].contains(query.toString())) {
                                    newTruckList.add(
                                        Truck(
                                        truckNameArray[i],
                                        truckProfileArray[i],
                                        isOpenArray[i],
                                        ratingArray[i],
                                        foodTypeArray[i]
                                    )
                                    )
                                    truckList.adapter = TruckAdapter(requireActivity(), newTruckList)
                                }
                            }
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            val newTruckList = ArrayList<Truck>()

                            for (i in truckNameArray.indices) {
                                if (truckNameArray[i].contains(newText!!)) {
                                    newTruckList.add(
                                        Truck(
                                        truckNameArray[i],
                                        truckProfileArray[i],
                                        isOpenArray[i],
                                        ratingArray[i],
                                        foodTypeArray[i]
                                    )
                                    )
                                    truckList.adapter = TruckAdapter(requireActivity(), newTruckList)
                                }
                            }
                            return false
                        }
                    })




                },
                { error -> Log.e("http", "$error")}
            )


        }


    }


}