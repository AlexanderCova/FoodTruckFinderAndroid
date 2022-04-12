package com.example.youfood

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import java.io.File

class SettingsScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_screen)

        val backButton = findViewById<ImageButton>(R.id.settingsBackButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        backButton.setOnClickListener{
            val intent = Intent(this, UserScreen::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        logoutButton.setOnClickListener {
            val file = File("records.txt")
            if (file.delete()) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }


        }
    }
}