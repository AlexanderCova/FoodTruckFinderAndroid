package com.foodtruckfindermi.client.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RatingBar
import android.widget.TextView
import com.foodtruckfindermi.client.R
import com.foodtruckfindermi.client.DataClasses.Review

class ReviewAdapter(private val context: Activity, private val arrayList: ArrayList<Review>) : ArrayAdapter<Review>(context,
    R.layout.list_item,arrayList) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate((R.layout.list_item), null)

        val authorLabel : TextView = view.findViewById(R.id.authorLabel)
        val bodyLabel : TextView = view.findViewById(R.id.bodyLabel)
        val dateLabel : TextView = view.findViewById(R.id.dateLabel)
        val ratingBar : RatingBar = view.findViewById(R.id.reviewItemRatingBar)


        authorLabel.text = arrayList[position].author
        bodyLabel.text = arrayList[position].body
        dateLabel.text = arrayList[position].date
        ratingBar.rating = arrayList[position].rating




        return view
    }
}