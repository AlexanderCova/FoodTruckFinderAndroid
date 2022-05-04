package com.example.youfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import  android.os.Bundle
import android.widget.ImageButton


class AccountScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_screen)

        val searchTabButton = findViewById<ImageButton>(R.id.searchTabButton)
        val eventTabButton = findViewById<ImageButton>(R.id.eventTabButton)


        searchTabButton.setOnClickListener {
            val intent = Intent(this, UserScreen::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        eventTabButton.setOnClickListener {
            val intent = Intent(this, EventsScreen::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}