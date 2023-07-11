package com.h2gether.homePage

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_MAIN
import android.content.Intent.CATEGORY_LAUNCHER
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.h2gether.R

class NotificationService : Service() {

    // Step 1: Declare necessary variables and constants
    private val channelId = "my_channel_id"
    private val channelName = "My Channel"
    private val notificationId = 1
    private var notificationsEnabled = false
    private val intervalMillis = 5000 // 5 seconds (temporary value)
    private var targetWater: Int = 0
    private var waterConsumed: Int = 0

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate() {
        super.onCreate()

        handler = Handler()
        runnable = Runnable { showNotification() }
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun showNotification() {
        TODO("Not yet implemented")
    }
}