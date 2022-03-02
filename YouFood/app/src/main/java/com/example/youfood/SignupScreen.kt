package com.example.youfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel

class SignupScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_screen)

        val signupButton = findViewById<Button>(R.id.signupButton)
        val emailEdit = findViewById<EditText>(R.id.signupEmailEdit)
        val passwordEdit = findViewById<EditText>(R.id.signupPasswordEdit)
        val errorText = findViewById<TextView>(R.id.signupErrorText)
        val loginButton = findViewById<Button>(R.id.redirectLoginButton)
        val backButton = findViewById<Button>(R.id.signupBackButton)


        signupButton.setOnClickListener {
            Fuel.get("https://foodtruckfindermi.com/user_auth?email=${emailEdit.text}&password=${passwordEdit.text}&type=LOGIN")
                .response { _request, _response, result ->
                    val (bytes) = result
                    if (bytes != null) {
                        var loginResult = "call ${String(bytes)}"

                        if (loginResult.equals("call true")) {
                            val intent = Intent(this, UserScreen::class.java)
                            startActivity(intent)
                        } else if(loginResult.equals("call false")) {
                            Toast.makeText(this, "Email is Already in Use. Try Logging in.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }


        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}