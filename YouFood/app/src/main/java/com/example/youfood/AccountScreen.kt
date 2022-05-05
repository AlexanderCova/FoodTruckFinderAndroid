package com.example.youfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import  android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import java.io.File


class AccountScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_screen)

        val searchTabButton = findViewById<ImageButton>(R.id.searchTabButton)
        val eventTabButton = findViewById<ImageButton>(R.id.eventTabButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)


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

        logoutButton.setOnClickListener {
            val file = File(filesDir, "records.txt")
            if (file.delete()) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }


        }
    }
}