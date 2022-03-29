package com.example.youfood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class EventInfoScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info_screen)

        val name = intent.getStringExtra("name")

        //TODO add server code to get event info based on name
        //TODO run http call to get that info and display it on screen




    }
}