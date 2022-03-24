package com.example.youfood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.coroutines.runBlocking

class ReviewScreen : AppCompatActivity() {

    private lateinit var reviewBodyArray : Array<String>
    private lateinit var reviewAuthorArray: Array<String>
    private lateinit var reviewDateArray: Array<String>

    private lateinit var reviewArrayList : ArrayList<Review>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_screen)

        val truckemail = intent.getStringArrayExtra("truckemail")
        val reviewList = findViewById<ListView>(R.id.reviewList)

        runBlocking {
            val (request, response, result) = Fuel.get("http://foodtruckfindermi.com/review-query?truck=${truckemail}").awaitStringResponseResult()

            result.fold(
                { data ->
                    var reviewArray = data.split("^").toTypedArray()
                    reviewAuthorArray = reviewArray[1].split("`").toTypedArray()
                    reviewAuthorArray = reviewAuthorArray.drop(1).toTypedArray()
                    reviewBodyArray = reviewArray[2].split("`").toTypedArray()
                    reviewBodyArray = reviewBodyArray.drop(1).toTypedArray()
                    reviewDateArray = reviewArray[3].split("`").toTypedArray()
                    reviewDateArray = reviewDateArray.drop(1).toTypedArray()

                    reviewArrayList = ArrayList()

                    for(i in reviewAuthorArray.indices){

                        val review = Review(reviewAuthorArray[i], reviewBodyArray[i], reviewDateArray[i])
                        reviewArrayList.add(review)
                    }

                    reviewList.adapter = ReviewAdapter(this@ReviewScreen, reviewArrayList)




                },
                { error -> Log.e("http" , "${error}")})
        }


    }



}