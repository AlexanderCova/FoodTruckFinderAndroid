package com.example.youfood.Fragments

import android.os.Bundle
import android.text.Html
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
import org.json.JSONObject


class TruckInfo (
    var name : String,
    var city : String,
    var email : String,
    var foodtype : String,
    var profile : String,
    var website : String,
    var isopen : String,
    var lon : String,
    var lat : String,
    var rating : String) {}




class TruckInfoFragment : Fragment() {

    private lateinit var truckinfo : TruckInfo

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
                    val jsonString = """
                        {
                            "Truck": $data
                        }
                    """.trimIndent()

                    val truckJsonObject = JSONObject(jsonString)
                    val truckObject = truckJsonObject.getJSONArray("Truck")

                    truckinfo = TruckInfo(truckObject.getJSONObject(0).getString("truckname"),
                        truckObject.getJSONObject(0).getString("city"),
                        truckObject.getJSONObject(0).getString("email"),
                        truckObject.getJSONObject(0).getString("foodtype"),
                        truckObject.getJSONObject(0).getString("profile"),
                        truckObject.getJSONObject(0).getString("website"),
                        truckObject.getJSONObject(0).getString("isopen"),
                        truckObject.getJSONObject(0).getString("lon"),
                        truckObject.getJSONObject(0).getString("lat"),
                        truckObject.getJSONObject(0).getString("rating"))

                    loadScreen(truckinfo)
                },
                { error ->
                    Log.e("http", "${error.exception}")
                }
            )
        }
    }


    private fun loadScreen(truck: TruckInfo) {
        val website = truck.website
        val isOpen = truck.isopen
        val city = truck.city
        val email = truck.email
        val food = truck.foodtype

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
            websiteLabel.text = Html.fromHtml("<a href=http://$website> Website")
            websiteLabel.movementMethod = LinkMovementMethod.getInstance()
            websiteLabel.visibility = View.VISIBLE
            websiteIcon.visibility = View.VISIBLE
        }

        foodLabel.text = food
        emailLabel.text = email

    }


}