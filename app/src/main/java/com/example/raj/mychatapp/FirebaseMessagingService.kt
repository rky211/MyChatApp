package com.example.raj.mychatapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(msg: RemoteMessage?) {
        super.onMessageReceived(msg)

        var title = msg?.notification?.title
        var bodyTxt = msg?.notification?.body
        var click_action = msg?.notification?.clickAction
        var from_userId = msg?.data?.get("from_user_id")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            var channel:NotificationChannel = NotificationChannel("general","MyNotification",NotificationManager.IMPORTANCE_DEFAULT)
            var manager:NotificationManager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        FirebaseMessaging.getInstance().subscribeToTopic("general").addOnCompleteListener {
            if (it.isSuccessful){

                Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show()
            }
        }

        // Create an explicit intent for an Activity in your app
        val intent = Intent(click_action)
                        .apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
        intent.putExtra("User_Id",from_userId)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)


        var mBuilder = NotificationCompat.Builder(this, "general")
                .setSmallIcon(R.drawable.my)
                .setContentTitle(title)
                .setContentText(bodyTxt)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // this is default for sending on notification \\var mNotifcationId:Int = 1;\\
        // to send multiple we need different id for each so here we take time and conver it t int
        var mNotifcationId = System.currentTimeMillis().toInt()
        var mNotifyManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotifyManager.notify(mNotifcationId,mBuilder.build())






    }
}
