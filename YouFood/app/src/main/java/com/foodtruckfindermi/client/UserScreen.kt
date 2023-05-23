package com.foodtruckfindermi.client

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.foodtruckfindermi.client.Adapter.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.foodtruckfindermi.client.Fragments.PremiumPopupFragment
import com.foodtruckfindermi.client.Fragments.TwoFactorLoginFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.qonversion.android.sdk.Qonversion
import java.io.File

class UserScreen : AppCompatActivity() {

    private lateinit var mViewPager: ViewPager
    private lateinit var searchBtn : ImageButton
    private lateinit var eventBtn : ImageButton
    private lateinit var messageBtn : ImageButton
    private lateinit var mPagerAdapter: PagerAdapter
    private lateinit var token: String

    private lateinit var mPagerViewAdapter: PagerAdapter
    lateinit var viewModel: qonversionViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_screen)


        mViewPager = findViewById(R.id.mViewPager)
        searchBtn = findViewById(R.id.searchBtn)
        eventBtn = findViewById(R.id.eventBtn)
        messageBtn = findViewById(R.id.messagesButton)

        val file = File(filesDir, "records.txt").readLines()
        val email = file[0]

        viewModel = qonversionViewModel()

        FirebaseApp.initializeApp(this)
        val db = Firebase.firestore

        searchBtn.setOnClickListener {
            mViewPager.currentItem = 0
        }

        eventBtn.setOnClickListener {
            mViewPager.currentItem = 1
        }


        mPagerViewAdapter = PagerAdapter(supportFragmentManager)
        mViewPager.adapter = mPagerViewAdapter
        mViewPager.offscreenPageLimit = 2








        if (viewModel.hasPremiumPermission) {
            messageBtn.setImageResource(R.drawable.ic_message_regular)
            messageBtn.setPadding(toDP(12), toDP(12), toDP(12), toDP(12))

            messageBtn.setOnClickListener {
                val intent = Intent(this, MessagesScreen::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
            }

            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("Messaging Token", task.exception.toString())
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                token = task.result
                Log.i("Messaging Token", token)


            })


            val userDoc = db.collection("Users").document(email)
            Log.i("Firebase", userDoc.toString())

            userDoc.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null) {
                        if (document.exists()) {

                            userDoc.set(hashMapOf("token" to token), SetOptions.merge())

                            return@addOnCompleteListener
                        } else {
                            val userData = hashMapOf(
                                "groups" to listOf<String>(),
                                "name" to email,
                                "token" to token
                            )

                            userDoc.set(userData)
                        }
                    }
                } else {
                    Log.d("TAG", "Error: ", task.exception)
                }
            }


        } else {
            messageBtn.setImageResource(R.drawable.ic_premium_messaging)
            messageBtn.setPadding(toDP(15), toDP(12), toDP(10), toDP(12))

            messageBtn.setOnClickListener {
                val popup = PremiumPopupFragment()
                popup.show(supportFragmentManager, "premium-popup")
            }
        }




        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                changeTabs(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })




        mViewPager.currentItem = 0
        searchBtn.setBackgroundResource(R.drawable.selected_tab_overlay)



    }


        private fun changeTabs(position: Int) {


        if (position == 0) {
            searchBtn.setBackgroundResource(R.drawable.selected_tab_overlay)
            eventBtn.setBackgroundResource(0)


        }
        if (position == 1) {
            searchBtn.setBackgroundResource(0)
            eventBtn.setBackgroundResource(R.drawable.selected_tab_overlay)

        }


    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        System.out.close()
    }

    fun refresh() {
        finish()
        startActivity(intent)
    }

    fun toDP(pixels : Int) : Int {
        return (pixels * this.resources.displayMetrics.density).toInt()
    }
}