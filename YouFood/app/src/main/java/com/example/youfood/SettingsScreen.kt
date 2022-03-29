package com.example.youfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class SettingsScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_screen)

        val backButton = findViewById<ImageButton>(R.id.settingsBackButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        backButton.setOnClickListener{
            val intent = Intent(this, UserScreen::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            val db = DBHelper(this, null)
            val cursor = db.getUser()

            cursor!!.moveToFirst()
            val email = cursor.getString(cursor.getColumnIndex("email").toInt())
            db.deleteUser(email)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}