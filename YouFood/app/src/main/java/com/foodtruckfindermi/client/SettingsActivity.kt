package com.foodtruckfindermi.client

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import java.io.File

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val file = File(filesDir, "records.txt")
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val enable2FAButton = findViewById<Button>(R.id.enable2FAButton)
        val settingsBackButton = findViewById<ImageButton>(R.id.settingsBackButton)

        val twoFactorAuth = file.readLines()[2]

        if (twoFactorAuth == "true") {
            enable2FAButton.visibility = View.GONE
        }



        logoutButton.setOnClickListener {
            file.delete()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }




        enable2FAButton.setOnClickListener {
            val intent = Intent(this, Enable2FAuthActivity::class.java)
            startActivity(intent)
        }

        settingsBackButton.setOnClickListener {
            onBackPressed()
        }

    }
}