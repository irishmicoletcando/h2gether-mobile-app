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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WaterDashboardPage : Fragment() {
    private var selectedOption: Int? = 0
    private var targetWater: Int? = 2200
    private var waterConsumed: Int? = 0
    private var percent: Int? = 0
    private var previousPercent: Int? = 0

    private lateinit var binding: FragmentWaterDashboardPageBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWaterDashboardPageBinding.inflate(inflater, container, false)

        // fetch water details and other initializations
        fetchWaterDetails()
        resetWaterConsumptionDaily()

        // fetch weather
        fetchWeather()

        // settings button
        binding.btnSettings.setOnClickListener {
            val settingsFragment = SettingsPage()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.constraint_layout, settingsFragment)
                .addToBackStack(null)
                .commit()
        }

        // Inflate the layout for this fragment
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // firebase initialize dependencies
        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("users/$uid/water-consumption")

        // button handlers

        val tint = context?.let { it1 -> ContextCompat.getColor(it1, R.color.azure) }

        binding.btnAddWater.setOnClickListener {
            if (waterConsumed!! < targetWater!!) {
                waterConsumed = selectedOption?.let { it1 -> waterConsumed?.plus(it1) }
                waterConsumed?.let { it1 -> setWaterDetails() }
                waterConsumed?.let { it1 ->
                    if (uid != null) {
                        selectedOption?.let { it2 -> previousPercent?.let { it3 ->
                            saveWaterConsumption(it1, it2,
                                it3
                            )
                        } }
                    }
                }
            } else {
                Toast.makeText(context, "Target water already achieved", Toast.LENGTH_SHORT).show()
                waterConsumed = selectedOption?.let { it1 -> waterConsumed?.plus(it1) }
                waterConsumed?.let { it1 -> setWaterDetails() }
                waterConsumed?.let { it1 ->
                    if (uid != null) {
                        selectedOption?.let { it2 -> previousPercent?.let { it3 ->
                            saveWaterConsumption(it1, it2,
                                it3
                            )
                        } }
                    }
                }
            }

        }

        binding.btnUndoWater.setOnClickListener {
            waterConsumed = selectedOption?.let { it1 -> waterConsumed?.minus(it1) }

            if (waterConsumed!! < 0) {
                waterConsumed = 0
            }

            waterConsumed?.let { setWaterDetails() }
            waterConsumed?.let { it1 ->
                if (uid != null) {
                    selectedOption?.let { it2 -> previousPercent?.let { it3 ->
                        saveWaterConsumption(it1, it2,
                            it3
                        )
                    } }
                }
            }
        }

        binding.op50ml.setOnClickListener{
            selectedOption = 50
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

        binding.op100ml.setOnClickListener{
            selectedOption = 100
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

        binding.op150ml.setOnClickListener{
            selectedOption = 150
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

        binding.op200ml.setOnClickListener{
            selectedOption = 200
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

        binding.op250ml.setOnClickListener{
            binding.iv50ml.colorFilter = null
            binding.iv100ml.colorFilter = null
            binding.iv150ml.colorFilter = null
            binding.iv200ml.colorFilter = null
            binding.iv250ml.colorFilter = null
            binding.ivCustom.colorFilter = null
            selectedOption = 250
            if (tint != null) {
                binding.iv250ml.setColorFilter(tint, PorterDuff.Mode.SRC_IN)
            }
        }

        binding.opCustom.setOnClickListener {
            val promptsView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_water_input_dialog, null)
            val userInput = promptsView.findViewById(R.id.etCustomInput) as TextInputLayout

            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setView(promptsView)

            alertDialogBuilder.setPositiveButton("OK") { dialog, id ->
                val inputText = userInput.editText!!.text.toString()
                if (!TextUtils.isEmpty(inputText)) {
                    binding.tvCustom.text = "$inputText ml"
                    selectedOption = inputText.toInt()
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

    }

    private fun setWaterDetails(){
        binding.tvRecommendedAmount.text = targetWater.toString()
        binding.tvAmountConsumed.text = waterConsumed.toString()
        previousPercent = percent
        percent = (((waterConsumed?.toFloat()!!) / targetWater?.toFloat()!!) * 100).toInt()
        startProgress()
        if (percent!! <= 100) {
            binding.tvPercent.text = percent.toString() + "%"
        } else {binding.tvPercent.text = "100%"}
    }

    private fun saveWaterConsumption(waterConsumed: Int,selectedOption: Int, previousPercent: Int){
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

    private fun startProgress() {
        val progressHandler = Handler()
        val maxProgress = percent
        var currentProgress = previousPercent

        val progressRunnable = object : Runnable {
            override fun run() {
                if (currentProgress != null) {
                    if (currentProgress < maxProgress!!) {
                            currentProgress += 1
                            if (currentProgress != null) {
                                binding.progressBar.progress = currentProgress
                            }
                            progressHandler.postDelayed(this, 5)
                    } else if (currentProgress > maxProgress!!) {
                        currentProgress -= 1
                        if (currentProgress != null) {
                            binding.progressBar.progress = currentProgress
                        }
                        progressHandler.postDelayed(this, 5)
                    }
                }
            }
        }

        progressHandler.postDelayed(progressRunnable, 500)
    }

    private fun fetchWaterDetails(){
        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val dataRef: DatabaseReference = database.child("users/$uid/water-consumption")

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Handle the retrieved data here
                if (dataSnapshot.exists()) {
                    val waterConsumption = dataSnapshot.getValue(WaterConsumptionDataModel::class.java)
                    // Process the retrieved value as needed

                    if (waterConsumption != null) {
                        waterConsumed = waterConsumption.waterConsumption
                        selectedOption = waterConsumption.selectedOption
                        previousPercent = waterConsumption.previousPercent
                    }
                } else {
                    // Data does not exist at the specified location
                    waterConsumed = 0
                }
                waterConsumed?.let { setWaterDetails() }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur during retrieval
            }
        }

        )


    }

    private fun resetWaterConsumptionDaily() {
        val sharedPref = requireContext().getSharedPreferences("H2getherSharedPreferences", Context.MODE_PRIVATE)
        val storedDate = sharedPref.getString("StoredDate", "")

        val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

        if (storedDate != currentDate) {
            // Reset the value to its initial state
            waterConsumed = 0

            // Update the stored date to the current date
            val editor = sharedPref.edit()
            editor.putString("StoredDate", currentDate)
            editor.apply()
        }
    }

    private fun fetchWeather(){
        val coroutineScope = CoroutineScope(Dispatchers.Main)

        // Call the fetchWeather function from a coroutine
        coroutineScope.launch {
            val weatherResponse = fetchWeatherFromOpenWeather()
            Log.i(ContentValues.TAG, weatherResponse.toString())
            if (weatherResponse != null) {
                val tempinCelcius = weatherResponse.weatherData.feels_like - 273.15
                binding.temperatureTextView.text = tempinCelcius.toInt().toString() + "°C"
                binding.weatherDescriptionTextView.text = weatherResponse.weatherDetails[0].description + " in ${weatherResponse.cityName}"
            }
        }
    }

    private suspend fun fetchWeatherFromOpenWeather(): WeatherResponse? {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val client = OpenWeatherMapApiClient(retrofit.create(OpenWeatherMapService::class.java))

        return client.getCurrentWeather("Manila")

    }

    class WaterConsumptionDataModel {
        @PropertyName("waterConsumption")
        var waterConsumption: Int? = 0
        var selectedOption: Int? = 0
        var previousPercent: Int? = 0
    }

    // Weather

    class OpenWeatherMapApiClient(private val service: OpenWeatherMapService) {
        suspend fun getCurrentWeather(location: String): WeatherResponse? {
            val response = service.getCurrentWeather(location, "4d7436b2e953a39a0d36c842e7742e02")
            Log.i(ContentValues.TAG, response.toString())
            if (response.isSuccessful) {
                return response.body()
            }
            return null
        }
    }

    interface OpenWeatherMapService {
        @GET("weather")
        suspend fun getCurrentWeather(
            @Query("q") location: String,
            @Query("appid") apiKey: String
        ): Response<WeatherResponse>
    }

    data class WeatherResponse(
        @SerializedName("name")
        val cityName: String,
        @SerializedName("main")
        val weatherData: WeatherData,
        @SerializedName("weather")
        val weatherDetails: List<WeatherDetails>
    )

    data class WeatherData(
        @SerializedName("temp")
        val temperature: Double,
        @SerializedName("humidity")
        val humidity: Double,
        @SerializedName("feels_like")
        val feels_like: Double
    )

    data class WeatherDetails(
        @SerializedName("description")
        val description: String,
    )
}