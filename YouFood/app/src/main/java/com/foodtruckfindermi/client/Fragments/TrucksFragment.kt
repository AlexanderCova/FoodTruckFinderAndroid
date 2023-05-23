package com.foodtruckfindermi.client.Fragments


import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import com.foodtruckfindermi.client.*
import com.foodtruckfindermi.client.Adapter.TruckAdapter
import com.foodtruckfindermi.client.DataClasses.Truck
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.android.synthetic.main.fragment_trucks.*
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.io.File


class TrucksFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trucks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearch()

        val file = File(requireActivity().filesDir, "records.txt")
        val activity = (activity as UserScreen)


        searchButton.setOnClickListener {
            var params = truckSearchView.layoutParams
            searchButton.visibility = View.GONE

            object : CountDownTimer(3000, 100) {
                override fun onTick(p0: Long) {
                    params.width += 100

                    truckSearchView.layoutParams = params
                }

                override fun onFinish() {
                    params.width = 350
                    truckSearchView.layoutParams = params
                }


            }


        }




    }


    private fun setupSearch() {


        runBlocking {
            val (_, _, result) = Fuel.get("http://foodtruckfindermi.com/truck-query")
                .awaitStringResponseResult()

            result.fold(
                { data ->
                    val json_string = """
                        {
                            "Trucks": $data
                            
                        }
                    """.trimIndent()
                    val searchView = truckSearchView

                    val truckArrayList = ArrayList<Truck>()

                    val truckJsonObject = JSONObject(json_string)
                    val truckObject = truckJsonObject.getJSONArray("Trucks")

                    for (i in 0 until(truckObject.length())) {
                        val truck = Truck(
                            truckObject.getJSONObject(i).getString("truckname"),
                            truckObject.getJSONObject(i).getString("profile"),
                            truckObject.getJSONObject(i).getString("isopen"),
                            truckObject.getJSONObject(i).getString("rating"),
                            truckObject.getJSONObject(i).getString("foodtype"))

                        truckArrayList.add(truck)
                    }
                    truckList.adapter = TruckAdapter(requireActivity(), truckArrayList)
                    truckList.setOnItemClickListener { _, _, position, _ ->

                        val intent = Intent(requireActivity(), TruckScreen::class.java)
                        intent.putExtra("TruckName", truckArrayList[position].name)
                        intent.putExtra("flag", "user")
                        startActivity(intent)
                    }

                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {

                            val newTruckList = ArrayList<Truck>()
                            searchView.clearFocus()
                            for (i in truckArrayList.indices) {
                                if (truckArrayList[i].name!!.contains(query.toString())) {
                                    newTruckList.add(
                                        truckArrayList[i]
                                    )
                                    truckList.adapter = TruckAdapter(requireActivity(), newTruckList)
                                }
                            }
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            val newTruckList = ArrayList<Truck>()

                            for (i in truckArrayList.indices) {
                                if (truckArrayList[i].name!!.contains(newText!!)) {
                                    newTruckList.add(
                                        truckArrayList[i]
                                    )
                                    truckList.adapter = TruckAdapter(requireActivity(), newTruckList)
                                }
                            }
                            return false
                        }
                    })




                },
                { error -> Log.e("http", "$error")}
            )


        }


    }


}