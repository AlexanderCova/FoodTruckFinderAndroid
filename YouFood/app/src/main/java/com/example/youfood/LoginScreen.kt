package com.example.youfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.runBlocking


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
            runBlocking {
                val (_request, _response, result) = Fuel.get("http://foodtruckfindermi.com/login?email=${emailEdit.text}&password=${passwordEdit.text}")
                    .awaitStringResponseResult()

                result.fold(
                    {data ->
                        if (data.equals("true")) {
                            val db = DBHelper(this@LoginScreen, null)
                            db.addUser(emailEdit.text.toString(), passwordEdit.text.toString())
                            startIntent()
                        } else if (data.equals("false")) {
                            val snackbar = Snackbar.make(
                                it, "Incorrect Credentials",
                                Snackbar.LENGTH_SHORT
                            ).setAction("Action", null)

                            snackbar.show()
                        }},
                    {error -> Log.e("http", "${error}")}
                )

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
    fun startIntent(){
        val intent = Intent(this, UserScreen::class.java)
        startActivity(intent)
    }
}