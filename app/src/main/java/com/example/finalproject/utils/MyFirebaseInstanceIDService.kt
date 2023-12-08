package com.example.finalproject.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import android.window.SplashScreen
import androidx.core.app.NotificationCompat
import com.example.finalproject.AppActivity
import com.example.finalproject.GroupRoomActivity
import com.example.finalproject.fragments.HomeFragment
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseInstanceIDService : FirebaseMessagingService() {
    var mNotificationManager: NotificationManager? = null


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)


// playing audio and vibration when user se reques
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val r = RingtoneManager.getRingtone(applicationContext, notification)
        r.play()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            r.isLooping = false
        }

        // vibration
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(100, 300, 300, 300)
        v.vibrate(pattern, -1)
        val resourceImage = resources.getIdentifier(
            remoteMessage.notification!!.icon, "drawable",
            packageName
        )

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "CHANNEL_ID")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            builder.setSmallIcon(R.drawable.icontrans);
            builder.setSmallIcon(resourceImage)
        } else {
//            builder.setSmallIcon(R.drawable.icon_kritikar);
            builder.setSmallIcon(resourceImage)
        }
        Log.d("test",remoteMessage.data.toString())
        val resultIntent = Intent(this, AppActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_IMMUTABLE)
        builder.setContentTitle(remoteMessage.notification!!.title)
        builder.setContentText(remoteMessage.notification!!.body)
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true)
        builder.setPriority(NotificationManager.IMPORTANCE_DEFAULT)
        mNotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "Your_channel_id"
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            mNotificationManager!!.createNotificationChannel(channel)
            builder.setChannelId(channelId)
        }


// notificationId is a unique int for each notification that you must define
        mNotificationManager!!.notify(100, builder.build())
    }
}