package com.example.youfood.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.youfood.EventInfoScreen
import com.example.youfood.R
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.android.synthetic.main.event_list_item.*
import kotlinx.android.synthetic.main.fragment_event_info.*
import kotlinx.coroutines.runBlocking

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
        val email = (activity as EventInfoScreen).email



        runBlocking {
            val (_, _, result) = Fuel.get(
                "http://foodtruckfindermi.com/get-event-info",
                listOf("name" to name, "account" to email)
            )
                .awaitStringResponseResult()
            result.fold(
                {data ->
                    var answer = data.split("`")
                    answer = answer.drop(1)

                    val desc = answer[1]
                    val date = answer[2]
                    val city = answer[3]

                    eventCityLabel.text = city
                    eventInfoDateLabel.text = date
                    eventDescLabel.text = desc

                },
                { error ->
                    Log.e("http", "${error.exception}")
                }
            )
        }
    }

}