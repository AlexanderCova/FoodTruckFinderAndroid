package com.example.youfood

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.runBlocking

class SignupScreen : AppCompatActivity(), TextWatcher {

    private var passwordScore = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_screen)

        val signupButton = findViewById<Button>(R.id.signupButton)
        val emailEdit = findViewById<EditText>(R.id.signupEmailEdit)
        val passwordEdit = findViewById<EditText>(R.id.signupPasswordEdit)
        val backButton = findViewById<Button>(R.id.signupBackButton)

        passwordEdit.addTextChangedListener(this)




        signupButton.setOnClickListener {
            runBlocking {
                val (_, _, result) = Fuel.post("http://foodtruckfindermi.com/signup", listOf("email" to emailEdit.text, "password" to passwordEdit.text))
                    .awaitStringResponseResult()

                result.fold( {data ->

                    if (data == "true") {
                        val db = DBHelper(this@SignupScreen, null)

                        db.addUser(emailEdit.text.toString(), passwordEdit.text.toString())
                        startIntent()
                    } else if (data == "false") {
                        val snackbar = Snackbar.make(
                            it, "Email Already Used",
                            Snackbar.LENGTH_SHORT
                        ).setAction("Action", null)

                        snackbar.show()

                    }
                             },
                    {error -> Log.e("http", "$error")})
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

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        val strength = calculateStrength(p0!!)

        val progressBar = findViewById<View>(R.id.progressBar) as ProgressBar
        val strengthView = findViewById<View>(R.id.strengthText) as TextView
        
        when (strength) {
            0 -> {
                progressBar.progress = 33
                progressBar.progressDrawable.setColorFilter(resources.getColor(R.color.true_red), PorterDuff.Mode.SRC_IN)
                strengthView.setText(R.string.weak)

            }
            1 -> {
                progressBar.progress = 66
                progressBar.progressDrawable.setColorFilter(resources.getColor(R.color.gold), PorterDuff.Mode.SRC_IN)
                strengthView.setText(R.string.medium)
            }
            3 -> {
                progressBar.progress = 100
                progressBar.progressDrawable.setColorFilter(resources.getColor(R.color.green), PorterDuff.Mode.SRC_IN)
                strengthView.setText(R.string.strong)
                passwordScore = 1
            }

        }
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    private fun calculateStrength(password: CharSequence): Int {
        var currentScore = 0
        var sawUpper = false
        var sawLower = false
        var sawDigit = false
        var sawSpecial = false


        for (i in 0 until password.length) {
            val c = password[i]

            if (!sawSpecial && !Character.isLetterOrDigit(c)) {
                currentScore += 1
                sawSpecial = true
            } else {
                if (!sawDigit && Character.isDigit(c)) {
                    currentScore += 1
                    sawDigit = true
                } else {
                    if (!sawUpper || !sawLower) {
                        if (Character.isUpperCase(c))
                            sawUpper = true
                        else
                            sawLower = true
                        if (sawUpper && sawLower)
                            currentScore += 1
                    }
                }
            }

        }

        if (password.length > REQUIRED_LENGTH) {
            if (REQUIRE_SPECIAL_CHARACTERS && !sawSpecial
                || REQUIRE_UPPER_CASE && !sawUpper
                || REQUIRE_LOWER_CASE && !sawLower
                || REQUIRE_DIGITS && !sawDigit) {
                currentScore = 1
            } else {
                currentScore = 2
                if (password.length > MAXIMUM_LENGTH) {
                    currentScore = 3
                }
            }
        } else {
            currentScore = 0
        }

        return currentScore
    }

    companion object {

        //This value defines the minimum length for the password
        internal var REQUIRED_LENGTH = 8

        //This value determines the maximum length of the password
        internal var MAXIMUM_LENGTH = 15

        //This value determines if the password should contain special characters. set it as "false" if you
        //do not require special characters for your password field.
        internal var REQUIRE_SPECIAL_CHARACTERS = true

        //This value determines if the password should contain digits. set it as "false" if you
        //do not require digits for your password field.
        internal var REQUIRE_DIGITS = true

        //This value determines if the password should require low case. Set it as "false" if you
        //do not require lower cases for your password field.
        internal var REQUIRE_LOWER_CASE = true

        //This value determines if the password should require upper case. Set it as "false" if you
        //do not require upper cases for your password field.
        internal var REQUIRE_UPPER_CASE = false
    }
}

