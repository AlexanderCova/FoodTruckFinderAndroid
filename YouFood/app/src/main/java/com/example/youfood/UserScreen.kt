package com.example.youfood

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.android.synthetic.main.activity_user_screen.*
import kotlinx.coroutines.runBlocking

class UserScreen : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_screen)

        val truckFragment = TrucksFragment()
        val eventFragment = EventFragment()
        val accountFragment = AccountFragment()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, truckFragment)
            addToBackStack(null)
            commit()
        }



        //Setup Tab Changing button
        val searchTabButton = findViewById<ImageButton>(R.id.searchTabButton)
        val accountTabButton = findViewById<ImageButton>(R.id.accountTabButton)
        val eventTabButton = findViewById<ImageButton>(R.id.eventTabButton)

        searchTabButton.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, truckFragment)
                addToBackStack(null)
                commit()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                searchTabButton.setColorFilter(this.getColor(R.color.gold), PorterDuff.Mode.SRC_IN)
                accountTabButton.setColorFilter(this.getColor(R.color.black), PorterDuff.Mode.SRC_IN)
                eventTabButton.setColorFilter(this.getColor(R.color.black), PorterDuff.Mode.SRC_IN)
            }
        }

        accountTabButton.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, accountFragment)
                addToBackStack(null)
                commit()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                searchTabButton.setColorFilter(this.getColor(R.color.black), PorterDuff.Mode.SRC_IN)
                accountTabButton.setColorFilter(this.getColor(R.color.gold), PorterDuff.Mode.SRC_IN)
                eventTabButton.setColorFilter(this.getColor(R.color.black), PorterDuff.Mode.SRC_IN)
            }
        }

        eventTabButton.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, eventFragment)
                addToBackStack(null)
                commit()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                searchTabButton.setColorFilter(this.getColor(R.color.black), PorterDuff.Mode.SRC_IN)
                accountTabButton.setColorFilter(this.getColor(R.color.black), PorterDuff.Mode.SRC_IN)
                eventTabButton.setColorFilter(this.getColor(R.color.gold), PorterDuff.Mode.SRC_IN)
            }
        }










    }



}