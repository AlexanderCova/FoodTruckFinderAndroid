package com.foodtruckfindermi.client

import android.app.*
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.io.File
import java.util.*

class MessageNotificationService : FirebaseMessagingService() {
    private val channelId = "Notification from Service"
    lateinit var email: String

    override fun onCreate() {
        super.onCreate()
        Log.d("Notifications", "Started Service")
        val file = File(filesDir, "records.txt").readLines()
        email = file[0]

        if (Build.VERSION.SDK_INT >= 26) {
            val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT
                )

            } else {
                TODO("VERSION.SDK_INT < O")
            }
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
        }
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Example Service")
            .setContentText("Running")
            .setSmallIcon(R.drawable.ic_food_truck)
            .setPriority(Notification.PRIORITY_MIN)







        startForeground(1, notification.build())
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("Messaging", "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            val intent = Intent(this, ChatScreen::class.java)
            intent.putExtra("groupID", remoteMessage.data["chatID"])
            intent.putExtra("email", email)

            Log.d("messaging", "Message data payload: ${remoteMessage.data}")
            val notification: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
                .setAutoCancel(true)
                .setOngoing(false)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle("Example Service")
                .setContentText(remoteMessage.data["message"])
                .setSmallIcon(R.drawable.ic_food_truck)
                .setContentIntent(PendingIntent.getActivity(this, 1, intent, 0))


            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as  NotificationManager
            notificationManager.notify(2, notification.build())
            Log.d("Notifications", "Built Notification")
        }
    }


}
