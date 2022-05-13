package com.example.youfood

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
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

        val trucksList = truckList
        setupSearch()

        trucksList.setOnItemClickListener { parent, _, i, _ ->
            val truckName = parent.getItemAtPosition(i) as String
            val intent = Intent(activity, TruckScreen::class.java)
            intent.putExtra("TruckName", truckName)
            startActivity(intent)
        }


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


                    var response = data.split("$")
                    response = response.drop(1)
                    Log.i("request", response.toString())

                    val truckArray = response.toTypedArray()


                    val searchView = searchView
                    val trucksList = truckList

                    val truckAdapter = array(truckArray)

                    trucksList.adapter = truckAdapter


                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            searchView.clearFocus()
                            if (truckArray.contains(query)) {
                                truckAdapter.filter.filter(query)
                            }

                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            truckAdapter.filter.filter(newText)
                            return false
                        }
                    })
                },
                { error -> Log.e("http", "$error")}
            )


        }


    }


}