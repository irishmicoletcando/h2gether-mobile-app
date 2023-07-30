package com.h2gether.homePage

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.h2gether.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotificationService : Service() {

    private val channelId = "my_channel_id"
    private val channelName = "My Channel"
    private val notificationId = 1
    private var notificationsEnabled = false
    private val intervalMillis = 7200000 //2hours
    private var targetWater: Int = 0
    private var waterConsumed: Int = 0

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate() {
        super.onCreate()

        handler = Handler()
        runnable = Runnable { showNotification() }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_START) {
            targetWater = intent.getIntExtra(EXTRA_TARGET_WATER, 0)
            waterConsumed = intent.getIntExtra(EXTRA_WATER_CONSUMED, 0)
            notificationsEnabled = true
            startForeground(notificationId, createNotification())
            handler.postDelayed(runnable, intervalMillis.toLong())
        } else if (intent?.action == ACTION_STOP) {
            stopForeground(true)
            stopSelf()
            notificationsEnabled = false
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, WaterDashboardPage::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Reminder Enabled")
            .setContentText("You will receive a notification every 2 hours.")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setAutoCancel(false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        return notificationBuilder.build()
    }

    private fun showNotification() {
        if (notificationsEnabled) {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val databaseReference = FirebaseDatabase.getInstance().getReference("users/$uid")
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val reminderSettings = snapshot.child("reminderSettings").getValue(Boolean::class.java)
                        notificationsEnabled = reminderSettings ?: false

                        if (notificationsEnabled) {
                            // Fetch the latest water consumption value
                            val waterConsumption = snapshot.child("water-consumption/waterConsumption").getValue(Int::class.java)
                            if (waterConsumption != null) {
                                // Update the waterConsumed variable with the latest value
                                waterConsumed = waterConsumption
                            }

                            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                            val remainingWater = targetWater - waterConsumed

                            if (remainingWater > 0) {
                                val notificationId = System.currentTimeMillis().toInt()
                                val notificationBuilder = NotificationCompat.Builder(this@NotificationService, channelId)
                                    .setSmallIcon(R.drawable.ic_notification)
                                    .setContentTitle("Reminder")
                                    .setContentText("Hydrate yourself! You still have $remainingWater ml of water left to drink.")
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setAutoCancel(false)

                                notificationManager.notify(notificationId, notificationBuilder.build())
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the cancellation
                }
            })
        }

        // Repeat the notification after the specified interval
        handler.postDelayed(runnable, intervalMillis.toLong())
    }



    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_TARGET_WATER = "EXTRA_TARGET_WATER"
        const val EXTRA_WATER_CONSUMED = "EXTRA_WATER_CONSUMED"
    }
}