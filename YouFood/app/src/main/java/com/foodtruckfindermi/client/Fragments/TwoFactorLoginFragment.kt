package com.foodtruckfindermi.client.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.foodtruckfindermi.client.LoginScreen
import com.foodtruckfindermi.client.R
import com.foodtruckfindermi.client.UserScreen
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.android.synthetic.main.fragment_two_factor_login.*
import kotlinx.coroutines.runBlocking
import java.io.File


class TwoFactorLoginFragment : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_two_factor_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = (activity as LoginScreen)
        val email = activity.email

        var code = ""

        runBlocking {
            val (_,_, result) = Fuel.get("http://foodtruckfindermi.com/get-2fa-code", listOf("email" to email)).awaitStringResponseResult()

            result.fold(
                {data ->
                    code = data
                },
                {error ->
                    Log.e("HTTP/2FA", "${error}")
                }
            )
        }

        authenticateButton.setOnClickListener {
            if (loginAuthCodeEdit.text.toString() == code) {
                val file = File(requireActivity().filesDir,"records.txt")
                val record = activity.emailEdit.text.toString() + "\n" + activity.passwordEdit.text.toString() + "\n" + "true"


                val intent = Intent(requireActivity(), UserScreen::class.java)
                startActivity(intent)
            }
        }
    }
}