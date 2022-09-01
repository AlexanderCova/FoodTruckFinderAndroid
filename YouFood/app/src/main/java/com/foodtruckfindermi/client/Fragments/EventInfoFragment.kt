package com.foodtruckfindermi.client.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foodtruckfindermi.client.EventInfoScreen
import com.foodtruckfindermi.client.R
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.android.synthetic.main.event_list_item.*
import kotlinx.android.synthetic.main.fragment_event_info.*
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class EventInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val name = (activity as EventInfoScreen).name



        runBlocking {
            val (_, _, result) = Fuel.get(
                "http://foodtruckfindermi.com/get-event-info",
                listOf("name" to name)
            ).awaitStringResponseResult()

            result.fold(
                {data ->
                    val jsonString = """
                        {
                            "Event": $data
                        }
                    """.trimIndent()


                    val eventJsonObject = JSONObject(jsonString)
                    val eventObject = eventJsonObject.getJSONArray("Event")

                    val desc = eventObject.getJSONObject(0).getString("desc")
                    val date = eventObject.getJSONObject(0).getString("date")
                    val city = eventObject.getJSONObject(0).getString("city")

                    eventCityLabel.text = city
                    eventInfoDateLabel.text = date
                    descriptionLabel.text = desc

                },
                { error ->
                    Log.e("http", "${error.exception}")
                }
            )
        }
    }

}