package com.example.youfood.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.youfood.Adapter.ReviewAdapter
import com.example.youfood.DataClasses.Review
import com.example.youfood.R
import com.example.youfood.TruckScreen
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.android.synthetic.main.fragment_truck_reviews.*
import kotlinx.coroutines.runBlocking
import java.io.File


class TruckReviewsFragment : Fragment() {

    private lateinit var reviewBodyArray : Array<String>
    private lateinit var reviewAuthorArray: Array<String>
    private lateinit var reviewDateArray: Array<String>
    private lateinit var reviewRatingArray: Array<String>

    private lateinit var reviewArrayList : ArrayList<Review>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_truck_reviews, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val reviewList = reviewList
        val submitReviewButton = submitReviewButton
        val bodyReviewTextEdit = bodyTextEdit
        val ratingBar = reviewRatingBar

        val file = File(requireActivity().filesDir, "records.txt").readLines()
        val email = file[0]

        val truckemail = (activity as TruckScreen).truckemail



        runBlocking {
            val (_, _, result) = Fuel.get("http://foodtruckfindermi.com/review-query?truck=${truckemail}")
                .awaitStringResponseResult()

            result.fold(
                { data ->
                    val reviewArray = data.split("^")


                    reviewAuthorArray = reviewArray[0].split("`").drop(1).toTypedArray()

                    reviewBodyArray = reviewArray[1].split("`").drop(1).toTypedArray()

                    reviewDateArray = reviewArray[2].split("`").drop(1).toTypedArray()

                    reviewRatingArray = reviewArray[3].split("`").drop(1).toTypedArray()

                    reviewArrayList = ArrayList()

                    for (i in reviewAuthorArray.indices) {

                        val review = Review(
                            reviewAuthorArray[i],
                            reviewBodyArray[i],
                            reviewDateArray[i],
                            reviewRatingArray[i].toFloat()
                        )
                        reviewArrayList.add(review)
                    }

                    reviewList.adapter = ReviewAdapter(requireActivity(), reviewArrayList)

                    var totalHeight = 0
                    for (i in 0 until reviewList.adapter.count) {
                        val listItem: View = reviewList.adapter.getView(i, null, reviewList)
                        listItem.measure(0, 0)
                        totalHeight += listItem.measuredHeight + listItem.measuredHeightAndState / 2
                    }
                    val params: ViewGroup.LayoutParams = reviewList.layoutParams
                    params.height =
                        totalHeight + reviewList.dividerHeight * (reviewList.adapter.count - 1)
                    reviewList.layoutParams = params
                    reviewList.requestLayout()

                },
                { error -> Log.e("http", "$error") })
        }


        submitReviewButton.setOnClickListener {
            runBlocking {
                val (_, _, result) = Fuel.post(
                    "http://foodtruckfindermi.com/create-review",
                    listOf(
                        "author" to email,
                        "body" to bodyReviewTextEdit.text,
                        "Truck" to truckemail,
                        "rating" to ratingBar.rating
                    )
                ).awaitStringResponseResult()

                result.fold(
                    { data ->
                        if (data == "true") {
                            bodyReviewTextEdit.text.clear()
                            ratingBar.rating = 0F
                        }
                    },
                    { error -> Log.e("http", "$error") }
                )

            }
        }
    }
}