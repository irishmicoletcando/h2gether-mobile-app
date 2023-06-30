package com.h2gether.homePage

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.PorterDuff
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
import androidx.core.content.ContextCompat
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
import com.h2gether.appUtils.WeatherUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WaterDashboardPage : Fragment() {
    private lateinit var binding: FragmentWaterDashboardPageBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    val AppUtils = com.h2gether.appUtils.AppUtils.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWaterDashboardPageBinding.inflate(inflater, container, false)

        // fetch water details and other initializations
        fetchWaterDetails()

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var weatherDetails = runBlocking { fetchWeatherDetails() }
        AppUtils.temperatureIndex = weatherDetails?.weatherData?.feels_like?.minus(
            273.15
        )!!.toInt()

        binding.temperatureTextView.text = AppUtils.temperatureIndex.toString()

        // firebase initialize dependencies
        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid
        databaseReference =
            FirebaseDatabase.getInstance().getReference("users/$uid/water-consumption")

        // button handlers
        val tint = context?.let { it1 -> ContextCompat.getColor(it1, R.color.azure) }

        binding.btnAddWater.setOnClickListener {
            if (AppUtils.waterConsumed!! < AppUtils.targetWater!!) {
                AppUtils.waterConsumed =
                    AppUtils.selectedOption?.let { it1 -> AppUtils.waterConsumed?.plus(it1) }
                AppUtils.waterConsumed?.let { it1 -> setWaterDetails() }
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
                Toast.makeText(context, "Target water already achieved", Toast.LENGTH_SHORT).show()
                AppUtils.waterConsumed =
                    AppUtils.selectedOption?.let { it1 -> AppUtils.waterConsumed?.plus(it1) }
                AppUtils.waterConsumed?.let { it1 -> setWaterDetails() }
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

        binding.btnUndoWater.setOnClickListener {
            AppUtils.waterConsumed =
                AppUtils.selectedOption?.let { it1 -> AppUtils.waterConsumed?.minus(it1) }

            if (AppUtils.waterConsumed!! < 0) {
                AppUtils.waterConsumed = 0
            }

            AppUtils.waterConsumed?.let { setWaterDetails() }
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

        binding.op50ml.setOnClickListener {
            AppUtils.selectedOption = 50
            binding.iv50ml.colorFilter = null
            binding.iv100ml.colorFilter = null
            binding.iv150ml.colorFilter = null
            binding.iv200ml.colorFilter = null
            binding.iv250ml.colorFilter = null
            binding.ivCustom.colorFilter = null
            if (tint != null) {
                binding.iv50ml.setColorFilter(tint, PorterDuff.Mode.SRC_IN)
            }
        }

        binding.op100ml.setOnClickListener {
            AppUtils.selectedOption = 100
            binding.iv50ml.colorFilter = null
            binding.iv100ml.colorFilter = null
            binding.iv150ml.colorFilter = null
            binding.iv200ml.colorFilter = null
            binding.iv250ml.colorFilter = null
            binding.ivCustom.colorFilter = null
            if (tint != null) {
                binding.iv100ml.setColorFilter(tint, PorterDuff.Mode.SRC_IN)
            }
        }

        binding.op150ml.setOnClickListener {
            AppUtils.selectedOption = 150
            binding.iv50ml.colorFilter = null
            binding.iv100ml.colorFilter = null
            binding.iv150ml.colorFilter = null
            binding.iv200ml.colorFilter = null
            binding.iv250ml.colorFilter = null
            binding.ivCustom.colorFilter = null
            if (tint != null) {
                binding.iv150ml.setColorFilter(tint, PorterDuff.Mode.SRC_IN)
            }
        }

        binding.op200ml.setOnClickListener {
            AppUtils.selectedOption = 200
            binding.iv50ml.colorFilter = null
            binding.iv100ml.colorFilter = null
            binding.iv150ml.colorFilter = null
            binding.iv200ml.colorFilter = null
            binding.iv250ml.colorFilter = null
            binding.ivCustom.colorFilter = null
            if (tint != null) {
                binding.iv200ml.setColorFilter(tint, PorterDuff.Mode.SRC_IN)
            }
        }

        binding.op250ml.setOnClickListener {
            binding.iv50ml.colorFilter = null
            binding.iv100ml.colorFilter = null
            binding.iv150ml.colorFilter = null
            binding.iv200ml.colorFilter = null
            binding.iv250ml.colorFilter = null
            binding.ivCustom.colorFilter = null
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

            binding.iv50ml.colorFilter = null
            binding.iv100ml.colorFilter = null
            binding.iv150ml.colorFilter = null
            binding.iv200ml.colorFilter = null
            binding.iv250ml.colorFilter = null
            binding.ivCustom.colorFilter = null
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

    }

    private fun setWaterDetails() {
        binding.tvRecommendedAmount.text = AppUtils.targetWater.toString()
        binding.tvAmountConsumed.text = AppUtils.waterConsumed.toString()
        AppUtils.previousPercent = AppUtils.percent
        AppUtils.percent =
            (((AppUtils.waterConsumed?.toFloat()!!) / AppUtils.targetWater?.toFloat()!!) * 100).toInt()
        delayProgress()
        if (AppUtils.percent!! <= 100) {
            binding.tvPercent.text = AppUtils.percent.toString() + "%"
        } else {
            binding.tvPercent.text = "100%"
        }
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

    private fun delayProgress() {
        val progressHandler = Handler()
        val maxProgress = AppUtils.percent
        var currentProgress = AppUtils.previousPercent

        val progressRunnable = object : Runnable {
            override fun run() {
                if (currentProgress != null) {
                    if (currentProgress < maxProgress!!) {
                        currentProgress += 1
                        binding.progressBar.progress = currentProgress
                        progressHandler.postDelayed(this, 5)
                    } else if (currentProgress > maxProgress!!) {
                        currentProgress -= 1
                        binding.progressBar.progress = currentProgress
                        progressHandler.postDelayed(this, 5)
                    }
                }
            }
        }

        progressHandler.postDelayed(progressRunnable, 500)
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
                        AppUtils.selectedOption = waterConsumption.selectedOption
                        AppUtils.previousPercent = waterConsumption.previousPercent
                    }
                } else {
                    // Data does not exist at the specified location
                    AppUtils.waterConsumed = 0
                }
                AppUtils.waterConsumed?.let { setWaterDetails() }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur during retrieval
            }
        }

        )


    }

    class WaterConsumptionDataModel {
        @PropertyName("waterConsumption")
        var waterConsumption: Int? = 0
        var selectedOption: Int? = 0
        var previousPercent: Int? = 0
    }

    private suspend fun fetchWeatherDetails(): WeatherUtils.WeatherResponse? {
        val weatherUtils = WeatherUtils()
        return weatherUtils.getWeatherDetails()
    }
}