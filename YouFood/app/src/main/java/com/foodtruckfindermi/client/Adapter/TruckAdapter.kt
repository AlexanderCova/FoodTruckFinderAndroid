package com.foodtruckfindermi.client.Adapter

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.foodtruckfindermi.client.DataClasses.Truck
import com.foodtruckfindermi.client.R
import com.foodtruckfindermi.client.R.id
import com.foodtruckfindermi.client.R.layout
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd


class TruckAdapter(private val context : Activity, private val arrayList : ArrayList<Truck>) :
    ArrayAdapter<Truck>(context, layout.truck_list_item, arrayList) {

    private lateinit var adLoader: AdLoader
    private var adList = mutableMapOf<Int, View>()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            if (((position + 1) % 5) == 0 && arrayList[position] != Truck("","","","", "")) {
                arrayList.add(position, Truck("","","","", ""))
            }

            if (((position + 1) % 5) == 0) {
                if (adList[position] == null) {

                    MobileAds.initialize(context)
                    // Production id ca-app-pub-9521285260882146/4870385084
                    //Test Id ca-app-pub-3940256099942544/2247696110

                    val inflater: LayoutInflater = LayoutInflater.from(context)
                    val view: View = inflater.inflate((layout.ad_view_list_item), null)



                    adLoader = AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
                        .forNativeAd(object : NativeAd.OnNativeAdLoadedListener {
                            private val background: ColorDrawable? = null
                            override fun onNativeAdLoaded(unifiedNativeAd: NativeAd) {


                                val styles =
                                    NativeTemplateStyle.Builder()
                                        .withMainBackgroundColor(background)
                                        .build()


                                val template = view.findViewById<TemplateView>(R.id.my_template)
                                template.setStyles(styles)
                                template.setNativeAd(unifiedNativeAd)

                                // Showing a simple Toast message to user when Native an ad is Loaded and ready to show

                            }
                        }).build()

                    val adRequest = AdRequest.Builder().build()

                    // load Native Ad with the Request

                    // load Native Ad with the Request
                    adLoader.loadAd(adRequest)

                    adList[position] = view

                    return view
                } else {
                    return adList[position]!!
                }
            } else {

                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate((layout.truck_list_item), null)

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
                    "0" -> isOpenLabel.text = "Closed"
                    "1" -> isOpenLabel.text = "Open"
                }

                ratingLabel.text = arrayList[position].rating + " Stars"
                foodTypeLabel.text = arrayList[position].foodType

                return view

            }

        }
    }