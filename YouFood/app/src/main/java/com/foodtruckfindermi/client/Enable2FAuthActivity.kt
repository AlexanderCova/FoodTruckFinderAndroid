package com.foodtruckfindermi.client

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.Group
import com.foodtruckfindermi.client.ui.CustomSpinner
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.coroutines.runBlocking
import java.io.File

class Enable2FAuthActivity : AppCompatActivity(), CustomSpinner.OnSpinnerEventsListener {

    private lateinit var providerSpinner: CustomSpinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enable2_fauth)

        providerSpinner = findViewById<CustomSpinner>(R.id.providerSpinner)

        providerSpinner.setSpinnerEventsListener(this);


        val getCodeButton = findViewById<Button>(R.id.getCodeButton)
        val phoneEdit = findViewById<EditText>(R.id.phoneNumberEdit)
        val codeEdit = findViewById<EditText>(R.id.authCodeEdit)
        val enable2FAButton = findViewById<Button>(R.id.enableButton)
        val verifyGroup = findViewById<Group>(R.id.verificationCodeGroup)
        var code = ""
        val phoneNumber = phoneEdit.text.toString()

        val file = File(filesDir, "records.txt").readLines()
        val email = file[0]
        val password = file[1]
        val provider = providerSpinner.selectedItem


        getCodeButton.setOnClickListener {

            Log.i("2FA", phoneEdit.text.toString())
            runBlocking {
                val (_, _, result) = Fuel.get("http://foodtruckfindermi.com/enable-2fa", listOf("provider" to providerSpinner.selectedItem, "phone" to phoneEdit.text.toString())).awaitStringResponseResult()

                result.fold(
                    {data ->
                        code = data
                        verifyGroup.visibility = View.VISIBLE
                    },
                    { error ->
                        Log.e("HTTP/2FA", "$error")
                    }
                )
            }

        }

        enable2FAButton.setOnClickListener {
            if (codeEdit.text.toString() == code) {
                runBlocking {
                    val (_, _, result) = Fuel.post(
                        "http://foodtruckfindermi.com/enable-2fa",
                        listOf("email" to email, "password" to password, "phone" to phoneEdit.text.toString(), "provider" to providerSpinner.selectedItem)
                    ).awaitStringResponseResult()

                    result.fold(
                        {data ->
                            if (data == "true") {
                                val intent = Intent(this@Enable2FAuthActivity, UserScreen::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val builder = AlertDialog.Builder(this@Enable2FAuthActivity)
                                builder.setTitle("Incorrect!!")
                                builder.setMessage("That code doesn't match!!")

                                builder.setNeutralButton("Try Again") { dialog, which ->
                                }

                                builder.show()
                            }
                        },
                        {error ->
                            Log.e("HTTP/2FA", "$error")
                        }
                    )

                }
            }
        }

    }

    override fun onPopupWindowOpened(spinner: Spinner?) {
        providerSpinner.background = resources.getDrawable(R.drawable.dropdown_down)
    }

    override fun onPopupWindowClosed(spinner: Spinner?) {
        providerSpinner.background = resources.getDrawable(R.drawable.dropdown_up)
    }
}