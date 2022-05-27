package com.example.youfood.Fragments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.youfood.R
import com.example.youfood.TruckScreen
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.android.synthetic.main.fragment_truck_info.*
import kotlinx.coroutines.runBlocking


class TruckInfoFragment : Fragment() {

    private lateinit var infoArray : Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_truck_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val truckName = (activity as TruckScreen).truckName

        runBlocking {
            val (_, _, result) = Fuel.get("http://foodtruckfindermi.com/get-truck-info?name=${truckName}").awaitStringResponseResult()
            result.fold(
                {data ->
                    var answer = data.split("`")
                    answer = answer.drop(1)
                    infoArray = answer.toTypedArray()

                    loadScreen(infoArray)
                },
                { error ->
                    Log.e("http", "${error.exception}")
                }
            )
        }
    }


    private fun loadScreen(info: Array<String>) {
        val website = info[6]
        val isOpen = info[7]
        val city = info[1]
        val email = info[2]
        val food = info[4]

        when (isOpen) {
            "0" ->  openLabel.text = getString(R.string.closed_hint)
            "1" ->  openLabel.text = getString(R.string.open_hint)
        }


        if (city != "" && city != "None"){
            cityLabel.text = city
            cityLabel.visibility = View.VISIBLE
            cityIcon.visibility = View.VISIBLE

        } else {
            cityLabel.visibility = View.GONE
            cityIcon.visibility = View.GONE
        }

        if (website == "") {
            websiteLabel.visibility = View.GONE
            websiteIcon.visibility = View.GONE
        } else {
            websiteLabel.text = "<a href='${website}'>Our Website</a>"
            websiteLabel.movementMethod = LinkMovementMethod.getInstance()
            websiteLabel.visibility = View.VISIBLE
            websiteIcon.visibility = View.VISIBLE
        }

        foodLabel.text = food
        emailLabel.text = email

    }


}