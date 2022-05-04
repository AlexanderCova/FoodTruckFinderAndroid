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
        val accountTabButton = findViewById<ImageButton>(R.id.accountTabButton)
        val eventTabButton = findViewById<ImageButton>(R.id.eventTabButton)
        val trucksList = findViewById<ListView>(R.id.truckList)



        setupSearch()


        accountTabButton.setOnClickListener {
            val intent = Intent(this, AccountScreen::class.java)
            startActivity(intent)
        }

        eventTabButton.setOnClickListener {
            val intent = Intent(this, EventsScreen::class.java)
            startActivity(intent)
        }






        trucksList.setOnItemClickListener { parent, _, i, _ ->
            val truckName = parent.getItemAtPosition(i) as String
            val intent = Intent(this, TruckScreen::class.java)
            intent.putExtra("TruckName", truckName)
            startActivity(intent)
        }





    }

    private fun array(array: Array<String>): ArrayAdapter<String> {

        return ArrayAdapter(
            this, android.R.layout.simple_list_item_1,
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


                    val searchView = findViewById<SearchView>(R.id.searchView)
                    val trucksList = findViewById<ListView>(R.id.truckList)

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