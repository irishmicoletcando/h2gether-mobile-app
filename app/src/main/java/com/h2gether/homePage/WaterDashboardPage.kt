package com.h2gether.homePage

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.lifecycleScope
import com.example.h2gether.R
import com.example.h2gether.databinding.FragmentWaterDashboardPageBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.PropertyName
import com.google.firebase.database.ValueEventListener
import com.google.gson.annotations.SerializedName
import com.h2gether.appUtils.AppUtils
import com.h2gether.appUtils.UserConfigUtils
import com.h2gether.appUtils.WaterPlanUtils
import com.h2gether.appUtils.WeatherUtils
import com.h2gether.userConfigActivities.WeightSelection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WaterDashboardPage : Fragment(), UserConfigUtils.UserConfigCallback {
    private lateinit var binding: FragmentWaterDashboardPageBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    val AppUtils = com.h2gether.appUtils.AppUtils.getInstance()
    val WeatherUtils = WeatherUtils()
    val UserConfigUtils = UserConfigUtils()
    val WaterPlanUtils = WaterPlanUtils()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWaterDashboardPageBinding.inflate(inflater, container, false)

        fetchWaterDetails()
        UserConfigUtils.setUserConfigurationDetails(this)
        WeatherUtils.setWeatherDetails()

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

        AppUtils.selectedOption = 0
        setTimer()

        // firebase initialize dependencies
        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid

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

        binding.btnSettings.setOnClickListener {
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
                    }
                } else {
                    // Data does not exist at the specified location
                    AppUtils.waterConsumed = 0
                    AppUtils.previousPercent = 0
                }

                AppUtils.waterConsumed?.let { setWaterDetails() }
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


    private fun setTimer(){
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, YourBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9) // Set the hour component to 10 (in 24-hour format)
            set(Calendar.MINUTE, 59)      // Set the minute component to 30
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
        private lateinit var firebaseAuth: FirebaseAuth
        val AppUtils = com.h2gether.appUtils.AppUtils.getInstance()
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
            val waterConsumedDaily = AppUtils.waterConsumed
            val date = AppUtils.getCurrentDate()
            firebaseAuth = FirebaseAuth.getInstance()
            val uid = firebaseAuth.currentUser?.uid
            databaseReference =
                FirebaseDatabase.getInstance().getReference("users/$uid/statistics")

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
            } else {
                Log.d(TAG, "stats: No uid")
            }

        }
    }
}