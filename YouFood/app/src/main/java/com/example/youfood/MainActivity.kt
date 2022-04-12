package com.example.youfood

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.database.getStringOrNull
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.httpGet
import java.io.File


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val file = File(filesDir,"records.txt")
        if (file.exists()){
            val intent = Intent(this, UserScreen::class.java)
            startActivity(intent)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val loginButton = findViewById<Button>(R.id.loginScreenButton)
        val signupButton = findViewById<Button>(R.id.signupScreenButton)

        loginButton.setOnClickListener {
            val intent = Intent(this, LoginScreen::class.java)
            startActivity(intent)
        }

        signupButton.setOnClickListener {
            val intent = Intent(this, SignupScreen::class.java)
            startActivity(intent)
        }
    }
}


