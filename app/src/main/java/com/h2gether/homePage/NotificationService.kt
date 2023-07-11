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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_START) {
            targetWater = intent.getIntExtra(EXTRA_TARGET_WATER, 0)
            waterConsumed = intent.getIntExtra(EXTRA_WATER_CONSUMED, 0)
            startForeground(notificationId, createNotification())
            notificationsEnabled = true
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

    private fun createNotification(): Notification? {
        //TODO: Not yet implemented
    }

    private fun showNotification() {
        TODO("Not yet implemented")
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_TARGET_WATER = "EXTRA_TARGET_WATER"
        const val EXTRA_WATER_CONSUMED = "EXTRA_WATER_CONSUMED"
    }
}