package com.foodtruckfindermi.client.Adapter

import android.app.Activity
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.*
import com.foodtruckfindermi.client.R.*
import com.foodtruckfindermi.client.DataClasses.Truck


class TruckAdapter(private val context : Activity, private val arrayList : ArrayList<Truck>) :
    ArrayAdapter<Truck>(context, layout.truck_list_item, arrayList) {



        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            val inflater : LayoutInflater = LayoutInflater.from(context)
            val view : View = inflater.inflate((layout.truck_list_item), null)

            val nameLabel = view.findViewById<TextView>(id.listTruckName)
            val profilePic = view.findViewById<ImageView>(id.listProfilePic)
            val isOpenLabel = view.findViewById<TextView>(id.listIsOpen)
            val ratingLabel = view.findViewById<TextView>(id.listRating)
            val foodTypeLabel = view.findViewById<TextView>(id.listFoodType)

            nameLabel.text = arrayList[position].name

            val bytes = Base64.decode(arrayList[position].profilePic, Base64.DEFAULT)
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            profilePic.setImageBitmap(bmp)

            when (arrayList[position].isOpen) {
                "0" ->  isOpenLabel.text = "Closed"
                "1" ->  isOpenLabel.text = "Open"
            }

            ratingLabel.text = arrayList[position].rating + " Stars"
            foodTypeLabel.text = arrayList[position].foodType

            return view

        }
    }