package com.example.youfood

import android.accounts.Account
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity

class UserScreen : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_screen)





        //Setup Tab Changing button
        val nearbyTabButton = findViewById<Button>(R.id.nearbyTabButton)

        nearbyTabButton.setOnClickListener {
            val intent = Intent(this, AccountInfoScreen::class.java)
            startActivity(intent)
        }


        setupSearchBar()
    }

    private fun setupSearchBar() {
        //seting up search bar for trucks

        val searchView = findViewById<SearchView>(R.id.searchView)
        val trucksList = findViewById<ListView>(R.id.truckList)
        val trucks = arrayOf("test1", "hello", "truck3")

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