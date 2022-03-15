package com.example.youfood

import android.accounts.Account
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.Fuel

class UserScreen : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_screen)

        Log.i("HELLO", "loaded")

        //Setup Tab Changing button
        val nearbyTabButton = findViewById<Button>(R.id.nearbyTabButton)
        val trucksList = findViewById<ListView>(R.id.truckList)


        nearbyTabButton.setOnClickListener {
            val intent = Intent(this, AccountInfoScreen::class.java)
            startActivity(intent)
        }



        Fuel.get("http://foodtruckfindermi.com/truck-query")
            .response { _request, _response, result ->
                val (bytes) = result
                Log.i("request", "request sent")
                if (bytes != null) {
                    var response = String(bytes).split("$")
                    response = response.drop(1)
                    Log.i("request", response.toString())

                    var truck_array = response.toTypedArray()



                    setupSearchBar(response)
                }

            }


        trucksList.setOnItemClickListener { parent, _, i, _ ->
            val truckName = parent.getItemAtPosition(i) as String
            val intent = Intent(this, TruckScreen::class.java)
            intent.putExtra("TruckName", truckName)
            startActivity(intent)
        }





    }

    private fun setupSearchBar(trucks : List<String>) {
        //seting up search bar for trucks

        val searchView = findViewById<SearchView>(R.id.searchView)
        val trucksList = findViewById<ListView>(R.id.truckList)

        val truckAdapter : ArrayAdapter<String> = ArrayAdapter(
            this, android.R.layout.simple_list_item_1,
            trucks
        )

        trucksList.adapter = truckAdapter


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                if (trucks.contains(query)) {
                    truckAdapter.filter.filter(query)
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean{
                truckAdapter.filter.filter(newText)
                return false
            }
        })
    }
}