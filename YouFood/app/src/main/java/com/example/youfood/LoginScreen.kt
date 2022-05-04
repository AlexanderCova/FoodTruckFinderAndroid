package com.example.youfood

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.runBlocking
import java.io.File


class LoginScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        val emailEdit = findViewById<EditText>(R.id.loginEmailEdit)
        val passwordEdit = findViewById<EditText>(R.id.loginPasswordEdit)
        val loginButton = findViewById<Button>(R.id.loginButton)

        val backButton = findViewById<Button>(R.id.loginBackButton)



        loginButton.setOnClickListener {
            runBlocking {
                val (_, _, result) = Fuel.get("http://foodtruckfindermi.com/login?email=${emailEdit.text}&password=${passwordEdit.text}")
                    .awaitStringResponseResult()

                result.fold(
                    {data ->
                        if (data == "true") {
                            val file = File(filesDir,"records.txt")
                            if (file.exists()) {
                                val record = emailEdit.text.toString() + "\n" + passwordEdit.text.toString()

                                openFileOutput("records.txt", Context.MODE_PRIVATE).use {
                                    it.write(record.toByteArray())
                                }
                            } else {
                                file.createNewFile()
                                val record = emailEdit.text.toString() + "\n" + passwordEdit.text.toString()

                                openFileOutput("records.txt", Context.MODE_PRIVATE).use {
                                    it.write(record.toByteArray())
                                }
                            }

                            startIntent()
                        } else if (data == "false") {
                            val snackbar = Snackbar.make(
                                it, "Incorrect Credentials",
                                Snackbar.LENGTH_SHORT
                            ).setAction("Action", null)

                            snackbar.show()
                        }},
                    {error -> Log.e("http", "$error")}
                )

            }
        }



        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun startIntent(){
        val intent = Intent(this, UserScreen::class.java)
        startActivity(intent)
    }
}