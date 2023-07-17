package com.h2gether.homePage

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Paint
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.h2gether.R
import com.example.h2gether.databinding.FragmentWaterDashboardPageBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.PropertyName
import com.google.firebase.database.ValueEventListener
import com.h2gether.appUtils.UserConfigUtils
import com.h2gether.appUtils.WaterPlanUtils
import com.h2gether.appUtils.WeatherUtils
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class WaterDashboardPage : Fragment(), UserConfigUtils.UserConfigCallback {
    private lateinit var binding: FragmentWaterDashboardPageBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var notificationServiceIntent: Intent
    private var isNotificationServiceRunning = false

    private var notificationsEnabled: Boolean = false

    val AppUtils = com.h2gether.appUtils.AppUtils.getInstance()
    val WeatherUtils = WeatherUtils()
    val UserConfigUtils = UserConfigUtils()
    val WaterPlanUtils = WaterPlanUtils()

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var broadcastReceiver: YourBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the broadcast receiver
        broadcastReceiver = YourBroadcastReceiver()

        // Register the broadcast receiver
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.example.ACTION_TIMER")
        requireActivity().registerReceiver(broadcastReceiver, intentFilter)
    }
    override fun onDestroy() {
        super.onDestroy()

        // Unregister the broadcast receiver
        requireActivity().unregisterReceiver(broadcastReceiver)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWaterDashboardPageBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            // Execute fetchWaterDetails() in the background
            val waterDetailsDeferred = async { fetchWaterDetails() }

            // Execute WeatherUtils.setWeatherDetails() in the background
            val weatherDetailsDeferred = async { WeatherUtils.setWeatherDetails() }

            // Wait for all the operations to complete
            waterDetailsDeferred.await()
            weatherDetailsDeferred.await()

            UserConfigUtils.setUserConfigurationDetails(this@WaterDashboardPage)

        }

        return binding.root
    }

    override fun onUserConfigFetched() {
        // The user configuration details are fetched and ready to use
        runBlocking { WaterPlanUtils.setTargetWater()
        }
        binding.targetWater = AppUtils.targetWater.toString()
        binding.waterConsumed = AppUtils.waterConsumed.toString()
        binding.temperature = AppUtils.temperatureIndex.toString() + "°C"
        AppUtils.percent =
            (((AppUtils.waterConsumed?.toFloat()!!) / AppUtils.targetWater?.toFloat()!!) * 100).toInt()
        if (AppUtils.percent!! < 100) {
            binding.percent = AppUtils.percent.toString() + "%"
        } else {
            binding.percent = "100%"
            Toast.makeText(context, "Target water already achieved", Toast.LENGTH_SHORT).show()
        }
        AppUtils.percent?.let { initializeProgressBar(0, it) }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isTimerReset = sharedPreferences.getBoolean("isTimerReset", false)
        Log.d("Content Values", isTimerReset.toString())
        if (!isTimerReset) {
            CoroutineScope(Dispatchers.Main).launch {
                // Start the timer process
                setTimer(AppUtils.hour, AppUtils.min)

                // Rest of the code that should execute after the timer process is done
                binding.waterConsumed = AppUtils.waterConsumed.toString()
                binding.temperature = AppUtils.temperatureIndex.toString() + "°C"
                AppUtils.percent = (((AppUtils.waterConsumed?.toFloat()!!) / AppUtils.targetWater?.toFloat()!!) * 100).toInt()
                if (AppUtils.percent!! < 100) {
                    binding.percent = AppUtils.percent.toString() + "%"
                } else {
                    binding.percent = "100%"
                    Toast.makeText(context, "Target water already achieved", Toast.LENGTH_SHORT).show()
                }
                AppUtils.percent?.let { initializeProgressBar(0, it) }

                // Set the flag to indicate that the timer has been reset
                sharedPreferences.edit().putBoolean("isTimerReset", true).apply()
            }
        }

        AppUtils.selectedOption = 0

        // firebase initialize dependencies
        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid

        // hello, (username) text
        fun calculateFontSizeToFitContainer(text: String, maxWidth: Float, maxChars: Int): Float {
            // Choose an initial font size
            var fontSize = 26f // Adjust the initial font size as desired

            val paint = Paint()
            paint.textSize = fontSize

            val charWidth = paint.measureText(text) / text.length
            val availableWidth = maxWidth - charWidth * 3 // Reserve space for the "..."
            val maxTextWidth = availableWidth.coerceAtMost(charWidth * maxChars)

            while (paint.measureText(text) > maxTextWidth && fontSize > 0) {
                fontSize -= 1f
                paint.textSize = fontSize
            }

            return fontSize
        }

        fun convertDpToPixels(dp: Float): Float {
            val scale = resources.displayMetrics.density
            return dp * scale + 0.5f
        }

        val username = binding.tvUsername
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        currentUser?.let {
            val emailRef = FirebaseDatabase.getInstance().getReference("users/$uid/email")
            emailRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val usernameText = "Hello, ${currentUser.email?.substringBefore("@gmail.com")}!"
                    username.text = usernameText
                    // Adjust the font size to fit the container and limit the number of characters
                    val maxWidthDp = 300f // specify the maximum width in dp
                    val maxWidthPx = convertDpToPixels(maxWidthDp)
                    val maxChars = 15 // specify the maximum number of characters
                    val fontSize = calculateFontSizeToFitContainer(usernameText, maxWidthPx, maxChars)
                    username.textSize = fontSize
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors that occur during email retrieval
                }
            })
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("users/$uid/water-consumption")

        // button handlers

        val tint = context?.let { it1 -> ContextCompat.getColor(it1, R.color.azure) }
        binding.btnAddWater.setOnClickListener {
            if (AppUtils.selectedOption == 0 ) {
                context?.let { it1 -> AppUtils.showToast("Please select an option", it1) }
            } else {
                if (AppUtils.waterConsumed!! < AppUtils.targetWater!!) {
                    AppUtils.waterConsumed =
                        AppUtils.selectedOption?.let { it1 -> AppUtils.waterConsumed?.plus(it1) }
                    AppUtils.waterConsumed?.let { setWaterDetails()
                    setProgressBar()}
                    AppUtils.waterConsumed?.let { it1 ->
                        if (uid != null) {
                            AppUtils.selectedOption?.let { it2 ->
                                AppUtils.previousPercent?.let { it3 ->
                                    saveWaterConsumption(
                                        it1, it2,
                                        it3
                                    )
                                }
                            }
                        }
                    }
                } else {
                    AppUtils.waterConsumed =
                        AppUtils.selectedOption?.let { it1 -> AppUtils.waterConsumed?.plus(it1) }
                    AppUtils.waterConsumed?.let { setWaterDetails()
                    setProgressBar()}
                    AppUtils.waterConsumed?.let { it1 ->
                        if (uid != null) {
                            AppUtils.selectedOption?.let { it2 ->
                                AppUtils.previousPercent?.let { it3 ->
                                    saveWaterConsumption(
                                        it1, it2,
                                        it3
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.btnUndoWater.setOnClickListener {
            if (AppUtils.selectedOption == 0 ) {
                context?.let { it1 -> AppUtils.showToast("Please select an option", it1) }
            } else {
            AppUtils.waterConsumed =
                AppUtils.selectedOption?.let { it1 -> AppUtils.waterConsumed?.minus(it1) }

            if (AppUtils.waterConsumed!! < 0) {
                AppUtils.waterConsumed = 0
            }
            AppUtils.waterConsumed?.let { setWaterDetails()
            setProgressBar()}
            AppUtils.waterConsumed?.let { it1 ->
                if (uid != null) {
                    AppUtils.selectedOption?.let { it2 ->
                        AppUtils.previousPercent?.let { it3 ->
                            saveWaterConsumption(
                                it1, it2,
                                it3
                            )
                        }
                    }
                }
            }
            }
        }

        binding.btnReminder.setOnClickListener{
            showConfirmationDialog()
        }

        binding.op50ml.setOnClickListener {
            AppUtils.selectedOption = 50
            decolorUnpressedIcons()
            if (tint != null) {
                binding.iv50ml.setColorFilter(tint, PorterDuff.Mode.SRC_IN)
            }
        }

        binding.op100ml.setOnClickListener {
            AppUtils.selectedOption = 100
            decolorUnpressedIcons()
            if (tint != null) {
                binding.iv100ml.setColorFilter(tint, PorterDuff.Mode.SRC_IN)
            }
        }

        binding.op150ml.setOnClickListener {
            AppUtils.selectedOption = 150
            decolorUnpressedIcons()
            if (tint != null) {
                binding.iv150ml.setColorFilter(tint, PorterDuff.Mode.SRC_IN)
            }
        }

        binding.op200ml.setOnClickListener {
            AppUtils.selectedOption = 200
            decolorUnpressedIcons()
            if (tint != null) {
                binding.iv200ml.setColorFilter(tint, PorterDuff.Mode.SRC_IN)
            }
        }

        binding.op250ml.setOnClickListener {
            decolorUnpressedIcons()
            AppUtils.selectedOption = 250
            if (tint != null) {
                binding.iv250ml.setColorFilter(tint, PorterDuff.Mode.SRC_IN)
            }
        }

        binding.opCustom.setOnClickListener {
            val promptsView = LayoutInflater.from(requireContext())
                .inflate(R.layout.custom_water_input_dialog, null)
            val userInput = promptsView.findViewById(R.id.etCustomInput) as TextInputLayout

            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setView(promptsView)

            alertDialogBuilder.setPositiveButton("OK") { dialog, id ->
                val inputText = userInput.editText!!.text.toString()
                if (!TextUtils.isEmpty(inputText)) {
                    binding.tvCustom.text = "$inputText ml"
                    AppUtils.selectedOption = inputText.toInt()
                }
            }.setNegativeButton("Cancel") { dialog, id ->
                dialog.cancel()
            }

            // Add any additional customization or functionality to the dialog
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            decolorUnpressedIcons()
            if (tint != null) {
                binding.ivCustom.setColorFilter(tint, PorterDuff.Mode.SRC_IN)
            }
        }

        binding.llSettings.setOnClickListener {
            val settingsFragment = SettingsPage()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.constraint_layout, settingsFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                // Execute fetchWaterDetails() in the background
                val waterDetailsDeferred = async { fetchWaterDetails() }

                // Execute UserConfigUtils.setUserConfigurationDetails(this) in the background
                val userConfigDeferred = async { UserConfigUtils.setUserConfigurationDetails(this@WaterDashboardPage) }

                // Execute WeatherUtils.setWeatherDetails() in the background
                val weatherDetailsDeferred = async { WeatherUtils.setWeatherDetails() }

                // Wait for all the operations to complete
                waterDetailsDeferred.await()
                userConfigDeferred.await()
                weatherDetailsDeferred.await()

                // All operations are completed, hide the loader

                binding.swipeRefreshLayout.isRefreshing = false
            }

        }

        notificationServiceIntent = Intent(requireContext(), NotificationService::class.java)
        isNotificationServiceRunning = isNotificationServiceRunning()

    }

    private fun showConfirmationDialog() {
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_layout, null)
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialogStyle)
            .setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.setOnShowListener {
            val positiveButton = dialogView.findViewById<Button>(R.id.dialog_positive_button)
            val negativeButton = dialogView.findViewById<Button>(R.id.dialog_negative_button)

            if (notificationsEnabled) {
                // If notifications are enabled, show the dialog to disable them
                val message = "Do you want to disable receiving notifications?"
                dialogView.findViewById<TextView>(R.id.dialog_message).text = message

                positiveButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.darkBlue
                    )
                )
                negativeButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.darkBlue
                    )
                )

                positiveButton.setOnClickListener {
                    Toast.makeText(requireContext(), "Notifications disabled", Toast.LENGTH_SHORT).show()
                    disableReminder()
                    alertDialog.dismiss()
                }

                negativeButton.setOnClickListener {
                    alertDialog.dismiss()
                }
            } else {
                // If notifications are disabled, show the dialog to enable them
                val message = "Do you want to enable receiving notifications every 2 hours?"
                dialogView.findViewById<TextView>(R.id.dialog_message).text = message

                positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkBlue))
                negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkBlue))

                val intervalMillis = 5000 //5 seconds temporary
                positiveButton.setOnClickListener {
                    enableReminder(intervalMillis)
                    alertDialog.dismiss()
                    Toast.makeText(requireContext(), "Notifications enabled", Toast.LENGTH_SHORT).show()
                }

                negativeButton.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
        }
        alertDialog.show()
    }

    private fun isNotificationServiceRunning(): Boolean {
        val manager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (NotificationService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun enableReminder(intervalMillis: Int) {
        Log.d("H2gether", "Starting notifications")
        notificationsEnabled = true

        val uid = firebaseAuth.currentUser?.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference("users/$uid")
        val reminderData = mapOf("reminderSettings" to notificationsEnabled)
        databaseReference.updateChildren(reminderData)

        val notificationServiceIntent = Intent(requireContext(), NotificationService::class.java).apply {
            action = NotificationService.ACTION_START
            putExtra(NotificationService.EXTRA_TARGET_WATER, AppUtils.targetWater) // Pass the value of targetWater
            putExtra(NotificationService.EXTRA_WATER_CONSUMED, AppUtils.waterConsumed) // Pass the value of waterConsumed
        }
        ContextCompat.startForegroundService(requireContext(), notificationServiceIntent)
    }

    private fun disableReminder() {
        Log.d("H2gether", "Stopping notifications")
        notificationsEnabled = false
        saveReminderSettings(false)
        if (isNotificationServiceRunning) {
            requireContext().stopService(notificationServiceIntent.apply {
                action = NotificationService.ACTION_STOP
            })
            isNotificationServiceRunning = false
        }
    }

    private fun saveReminderSettings(enabled: Boolean) {
        val uid = firebaseAuth.currentUser?.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference("users/$uid")
        val reminderData = mapOf("reminderSettings" to enabled)
        databaseReference.updateChildren(reminderData)
    }

    private fun fetchReminderSettings() {
        val uid = firebaseAuth.currentUser?.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference("users/$uid")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val reminderSettings = snapshot.child("reminderSettings").getValue(Boolean::class.java)
                    notificationsEnabled = reminderSettings ?: false
                    if (!notificationsEnabled && isNotificationServiceRunning) {
                        requireContext().stopService(notificationServiceIntent.apply {
                            action = NotificationService.ACTION_STOP
                        })
                        isNotificationServiceRunning = false
                    } else if (notificationsEnabled && !isNotificationServiceRunning) {
                        ContextCompat.startForegroundService(requireContext(), notificationServiceIntent.apply {
                            action = NotificationService.ACTION_START
                            putExtra(NotificationService.EXTRA_TARGET_WATER, AppUtils.targetWater) // Pass the value of targetWater
                            putExtra(NotificationService.EXTRA_WATER_CONSUMED, AppUtils.waterConsumed) // Pass the value of waterConsumed
                        })
                        isNotificationServiceRunning = true
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the cancellation
            }
        })
    }

    override fun onStart() {
        super.onStart()
        fetchReminderSettings()
    }

    override fun onStop() {
        super.onStop()
        if (!notificationsEnabled && isNotificationServiceRunning) {
            requireContext().stopService(notificationServiceIntent.apply {
                action = NotificationService.ACTION_STOP
            })
            isNotificationServiceRunning = false
        }
    }

    private fun fetchWaterDetails() {
        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val dataRef: DatabaseReference = database.child("users/$uid/water-consumption")

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Handle the retrieved data here
                if (dataSnapshot.exists()) {
                    val waterConsumption =
                        dataSnapshot.getValue(WaterConsumptionDataModel::class.java)
                    // Process the retrieved value as needed

                    if (waterConsumption != null) {
                        AppUtils.waterConsumed = waterConsumption.waterConsumption
                        AppUtils.previousPercent = waterConsumption.previousPercent
                        AppUtils.waterConsumed?.let { setWaterDetails() }
                        AppUtils.percent =
                            (((AppUtils.waterConsumed?.toFloat()!!) / AppUtils.targetWater?.toFloat()!!) * 100).toInt()
                        if (AppUtils.percent!! < 100) {
                            binding.percent = AppUtils.percent.toString() + "%"
                        } else {
                            binding.percent = "100%"
                            Toast.makeText(context, "Target water already achieved", Toast.LENGTH_SHORT).show()
                        }
                        AppUtils.percent?.let { initializeProgressBar(0, it) }
                    }
                } else {
                    // Data does not exist at the specified location
                    AppUtils.waterConsumed = 0
                    AppUtils.previousPercent = 0
                }


            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur during retrieval
            }
        }

        )


    }

    private fun saveWaterConsumption(
        waterConsumed: Int,
        selectedOption: Int,
        previousPercent: Int
    ) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val existingData: Map<String, Any>? = snapshot.value as? Map<String, Any>

                // Create a new map that includes the existing data and the new field
                val newData = existingData?.toMutableMap() ?: mutableMapOf()
                newData["waterConsumption"] = waterConsumed
                newData["selectedOption"] = selectedOption
                newData["previousPercent"] = previousPercent

                databaseReference.updateChildren(newData)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setWaterDetails() {
        binding.targetWater = AppUtils.targetWater.toString()
        binding.waterConsumed = AppUtils.waterConsumed.toString()
        binding.temperature = AppUtils.temperatureIndex.toString() + "°C"

    }

    private fun setProgressBar() {
        AppUtils.previousPercent = AppUtils.percent
        AppUtils.percent =
            (((AppUtils.waterConsumed?.toFloat()!!) / AppUtils.targetWater?.toFloat()!!) * 100).toInt()
        binding.percent = AppUtils.percent.toString()

        if (AppUtils.percent!! < 100) {
            binding.percent = AppUtils.percent.toString() + "%"
        } else {
            binding.percent = "100%"
            Toast.makeText(context, "Target water already achieved", Toast.LENGTH_SHORT).show()
        }

        val progressHandler = Handler()
        val progressRunnable = object : Runnable {
            override fun run() {
                if (AppUtils.previousPercent!! < AppUtils.percent!!) {
                    AppUtils.previousPercent = AppUtils.previousPercent!! + 1
                    binding.progressBar.progress = AppUtils.previousPercent!!
                    progressHandler.postDelayed(this, 5)
                } else if (AppUtils.previousPercent!! > AppUtils.percent!!) {
                    AppUtils.previousPercent = AppUtils.previousPercent!! - 1
                    binding.progressBar.progress = AppUtils.previousPercent!!
                    progressHandler.postDelayed(this, 5)
                }
            }
        }
        progressHandler.postDelayed(progressRunnable, 500)

    }

    private fun initializeProgressBar(prev: Int, max: Int){
        val progressHandler = Handler()
        var prev = prev
        var max = max
        val progressRunnable = object : Runnable {
            override fun run() {
                if (prev!! < max!!) {
                    prev = prev!! + 1
                    binding.progressBar.progress = prev!!
                    progressHandler.postDelayed(this, 5)
                } else if (prev!! > max!!) {
                    prev = prev!! - 1
                    binding.progressBar.progress = prev!!
                    progressHandler.postDelayed(this, 5)
                }
            }
        }
        progressHandler.postDelayed(progressRunnable, 500)
    }

    private fun decolorUnpressedIcons(){
        binding.iv50ml.colorFilter = null
        binding.iv100ml.colorFilter = null
        binding.iv150ml.colorFilter = null
        binding.iv200ml.colorFilter = null
        binding.iv250ml.colorFilter = null
        binding.ivCustom.colorFilter = null
    }

    class WaterConsumptionDataModel {
        @PropertyName("waterConsumption")
        var waterConsumption: Int? = 0
        var selectedOption: Int? = 0
        var previousPercent: Int? = 0
    }

    // Store water consumption daily for statistics

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setTimer(hour: Int?, min: Int?) {
        Log.d("contentValues", "Timer Set")
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, YourBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent) // Cancel any existing alarms with the same PendingIntent

        val calendar = Calendar.getInstance().apply {
            if (hour != null) {
                set(Calendar.HOUR_OF_DAY, hour)
            } // Set the hour component
            if (min != null) {
                set(Calendar.MINUTE, min)
            } // Set the minute component
            set(Calendar.SECOND, 0)
            set(Calendar.AM_PM, Calendar.PM)
        }

        // Set the alarm to trigger every day at the specified time
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    class YourBroadcastReceiver : BroadcastReceiver() {
        private lateinit var databaseReference: DatabaseReference
        private lateinit var databaseReference2: DatabaseReference
        private lateinit var firebaseAuth: FirebaseAuth
        val AppUtils = com.h2gether.appUtils.AppUtils.getInstance()

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("contentValues", "Received broadcast")
            val waterConsumedDaily = AppUtils.waterConsumed
            AppUtils.waterConsumed = 0

            val date = AppUtils.getCurrentDate()
            firebaseAuth = FirebaseAuth.getInstance()
            val uid = firebaseAuth.currentUser?.uid
            databaseReference =
                FirebaseDatabase.getInstance().getReference("users/$uid/statistics")
            databaseReference2 =
                FirebaseDatabase.getInstance().getReference("users/$uid/water-consumption")

            if (uid != null) {
                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val existingData: Map<String, Any>? = snapshot.value as? Map<String, Any>

                        // Create a new map that includes the existing data and the new field
                        val newData = existingData?.toMutableMap() ?: mutableMapOf()
                        if (waterConsumedDaily != null) {
                            newData[date] = waterConsumedDaily.toInt()
                        }

                        databaseReference.updateChildren(newData)
                            .addOnSuccessListener {
                                Log.d(TAG, "stats: data uploaded")
                            }
                            .addOnFailureListener {
                                Log.d(TAG, "stats: data NOT uploaded")
                            }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle the cancellation
                    }
                })
                databaseReference2.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val existingData: Map<String, Any>? = snapshot.value as? Map<String, Any>

                        // Create a new map that includes the existing data and the new field
                        val newData = existingData?.toMutableMap() ?: mutableMapOf()
                        if (waterConsumedDaily != null) {
                            newData["waterConsumption"] = AppUtils.waterConsumed!!.toInt()
                        }

                        databaseReference2.updateChildren(newData)
                            .addOnSuccessListener {
                                Log.d(TAG, "stats: data uploaded")
                            }
                            .addOnFailureListener {
                                Log.d(TAG, "stats: data NOT uploaded")
                            }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle the cancellation
                    }
                })
            } else {
                Log.d(TAG, "stats: No uid")
            }

        }
    }
}