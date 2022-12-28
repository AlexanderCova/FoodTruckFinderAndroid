package com.foodtruckfindermi.client

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.createBalloon

class CreateEventActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        val privateHelpButton = findViewById<ImageView>(R.id.privateHelpButton)


        privateHelpButton.setOnClickListener {
            val balloon = createBalloon(baseContext) {
                setArrowSize(10)
                setWidthRatio(1.0f)
                setHeight(65)
                setArrowPosition(0.7f)
                setCornerRadius(4f)
                setAlpha(0.9f)
                setText("When the event is private, only you can invite trucks to your event. When not private, trucks can request to come to your event, then giving you the opportunity to chat with them!! ")
                setTextColorResource(R.color.white)
                setIconDrawable(ContextCompat.getDrawable(baseContext, R.drawable.ic_baseline_help_outline_24))
                setBackgroundColorResource(R.color.gold)
                setBalloonAnimation(BalloonAnimation.FADE)
                setLifecycleOwner(lifecycleOwner)
            }
        }

    }
}