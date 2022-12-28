package com.foodtruckfindermi.client

import android.app.Application
import com.qonversion.android.sdk.Qonversion

class QonversionApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Qonversion.setDebugMode()
        Qonversion.launch(this, "NjxeYGBTmcjUdV31HJjf5zzel3o1FU7l", false)

    }
}