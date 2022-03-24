package com.example.youfood

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.coroutines.runBlocking

class UserScreen : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_screen)

        //Setup Tab Changing button
        val nearbyTabButton = findViewById<Button>(R.id.nearbyTabButton)
        val trucksList = findViewById<ListView>(R.id.truckList)


        setup_search()


        nearbyTabButton.setOnClickListener {
            val intent = Intent(this, AccountInfoScreen::class.java)
            startActivity(intent)
        }




        trucksList.setOnItemClickListener { parent, _, i, _ ->
            val truckName = parent.getItemAtPosition(i) as String
            val intent = Intent(this, TruckScreen::class.java)
            intent.putExtra("TruckName", truckName)
            startActivity(intent)
        }





    }

    fun array(array: Array<String>) : ArrayAdapter<String> {
        val truckAdapter: ArrayAdapter<String> = ArrayAdapter(
            this, android.R.layout.simple_list_item_1,
            array
        )

        return truckAdapter
    }

    fun setup_search() {

        runBlocking {
            val (request, _response, result) = Fuel.get("http://foodtruckfindermi.com/truck-query")
                .awaitStringResponseResult()

            result.fold(
                { data ->


                    var response = data.split("$")
                    response = response.drop(1)
                    Log.i("request", response.toString())

                    var truck_array = response.toTypedArray()


                    val searchView = findViewById<SearchView>(R.id.searchView)
                    val trucksList = findViewById<ListView>(R.id.truckList)

                    val truckAdapter = array(truck_array)

                    trucksList.adapter = truckAdapter


                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            searchView.clearFocus()
                            if (truck_array.contains(query)) {
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
                { error -> Log.e("http", "${error}")}
            )


        }


    }

}