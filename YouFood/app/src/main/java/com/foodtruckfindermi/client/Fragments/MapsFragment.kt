package com.foodtruckfindermi.client.Fragments

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foodtruckfindermi.client.R
import com.foodtruckfindermi.client.TruckScreen

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_maps.*
import kotlinx.android.synthetic.main.fragment_truck_info.*

class MapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val lat = (activity as TruckScreen).lat
        val lon = (activity as TruckScreen).lon
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(lat, lon)))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lon)))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 15F))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        val activity = (activity as TruckScreen)

        when (activity.isOpen) {
            "0" ->  {
                mapLayout?.visibility = View.GONE
                closedText.visibility = View.VISIBLE
            }
            "1" -> {
                mapLayout?.visibility = View.VISIBLE
                closedText.visibility = View.GONE

            }
        }
    }
}