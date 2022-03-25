package com.example.youfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
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

        val truckemail = intent.getStringExtra("truckemail")
        val truckName = intent.getStringExtra("TruckName")
        val reviewList = findViewById<ListView>(R.id.reviewList)
        val submitReviewButton = findViewById<Button>(R.id.submitReviewButton)
        val bodyReviewTextEdit = findViewById<EditText>(R.id.bodyTextEdit)
        val backButton = findViewById<ImageButton>(R.id.reviewBackButton)
        val truckNameLabel = findViewById<TextView>(R.id.reviewNameLabel)

        truckNameLabel.text = truckName



        runBlocking {
            val (request, response, result) = Fuel.get("http://foodtruckfindermi.com/review-query?truck=${truckemail}")
                .awaitStringResponseResult()

            result.fold(
                { data ->
                    var reviewArray = data.split("^")
                    Log.i("Arrays", reviewArray[0].toString())

                    reviewAuthorArray = reviewArray[0].split("`").drop(1).toTypedArray()

                    reviewBodyArray = reviewArray[1].split("`").drop(1).toTypedArray()

                    reviewDateArray = reviewArray[2].split("`").drop(1).toTypedArray()

                    reviewArrayList = ArrayList()

                    for(i in reviewAuthorArray.indices){

                        val review = Review(reviewAuthorArray[i], reviewBodyArray[i], reviewDateArray[i])
                        reviewArrayList.add(review)
                    }

                    reviewList.adapter = ReviewAdapter(this@ReviewScreen, reviewArrayList)

                },
                { error -> Log.e("http", "${error}") })
        }


        submitReviewButton.setOnClickListener {
            runBlocking {

                val db = DBHelper(this@ReviewScreen, null)

                val cursor = db.getUser()

                cursor!!.moveToFirst()
                val email = cursor.getString(cursor.getColumnIndex("email").toInt())


                val (request, response, result) = Fuel.post(
                    "http://foodtruckfindermi.com/create-review",
                    listOf("author" to email, "body" to bodyReviewTextEdit.text, "truck" to truckemail)
                ).awaitStringResponseResult()
            }
        }

        backButton.setOnClickListener {
            val intent = Intent(this, TruckScreen::class.java)
            intent.putExtra("TruckName", truckName)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }



    }

}