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


        nearbyTabButton.setOnClickListener {
            val intent = Intent(this, AccountInfoScreen::class.java)
            startActivity(intent)
        }



        Fuel.get("http://foodtruckfindermi.com/truck-query")
            .response { _request, _response, result ->
                val (bytes) = result
                if (bytes != null) {
                    var response = String(bytes).split("$")
                    response = response.drop(1)

                    var truck_array = response.toTypedArray()



                    setupSearchBar(truck_array)
                }

            }



    }

    private fun setupSearchBar(trucks : Array<String>) {
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