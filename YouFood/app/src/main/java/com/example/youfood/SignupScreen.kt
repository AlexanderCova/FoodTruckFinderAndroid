package com.example.youfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.runBlocking

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


        signupButton.setOnClickListener { it ->
            runBlocking {
                val (_request, _response, result) = Fuel.post("http://foodtruckfindermi.com/signup", listOf("email" to emailEdit.text, "password" to passwordEdit.text))
                    .awaitStringResponseResult()

                result.fold( {data ->

                    if (data.equals("true")) {
                        startIntent()
                    } else if (data.equals("false")) {
                        val snackbar = Snackbar.make(
                            it, "Email Already Used",
                            Snackbar.LENGTH_SHORT
                        ).setAction("Action", null)

                        snackbar.show()

                    }
                             },
                    {error -> Log.e("http", "${error}")})
            }
        }



        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun startIntent(){
        val intent = Intent(this, UserScreen::class.java)
        startActivity(intent)
    }
}
