package com.example.youfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.github.kittinunf.fuel.Fuel

class LoginScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        val emailEdit = findViewById<EditText>(R.id.loginEmailEdit)
        val passwordEdit = findViewById<EditText>(R.id.loginPasswordEdit)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val errorText = findViewById<TextView>(R.id.loginErrorText)
        val signupButton = findViewById<Button>(R.id.redirectSignupButton)
        val backButton = findViewById<Button>(R.id.loginBackButton)



        loginButton.setOnClickListener {
            Fuel.get("https://foodtruckfindermi.com/login?email=${emailEdit.text}&password=${passwordEdit.text}&type=LOGIN")
                .response { _request, _response, result ->

                    val (bytes) = result
                    if (bytes != null) {
                        var loginResult = "call ${String(bytes)}"

                        if (loginResult.equals("call true")) {
                            val intent = Intent(this, UserScreen::class.java)
                            startActivity(intent)
                        } else if(loginResult.equals("call false")) {
                            errorText.text = "Invalid Credentials\nCreate New Account?"
                            errorText.visibility = View.VISIBLE
                            signupButton.visibility = View.VISIBLE
                        }
                    }
                }
        }

        signupButton.setOnClickListener {
            val intent = Intent(this, SignupScreen::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}