package com.h2gether.homePage

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.example.h2gether.R
import com.example.h2gether.databinding.FragmentWeatherPageBinding
import com.h2gether.appUtils.AppUtils
import com.h2gether.appUtils.WeatherUtils
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Timer
import java.util.TimerTask

class WeatherPage : Fragment() {
    private lateinit var binding: FragmentWeatherPageBinding
    val AppUtils = com.h2gether.appUtils.AppUtils.getInstance()
    val weatherUtils = WeatherUtils()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentWeatherPageBinding.inflate(inflater, container, false)
        setToolBar("Weather")

        updateUI()

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchWeatherPeriodically(1)

    }

    private fun setToolBar(title: String){
        binding.toolbarLayout.logoutButton.visibility = View.GONE
        binding.toolbarLayout.backButton.visibility = View.GONE
        binding.toolbarLayout.toolbarTitle.text = title
    }

    private fun setWeatherImage(imageReference: String) {
        return when (imageReference) {
            "Clear", -> binding.ivTemperatureIcon.setImageResource(R.drawable.sunny_weather)
            "Haze","Clouds","Mist","Fog"  -> binding.ivTemperatureIcon.setImageResource(R.drawable.cloudy_weather)
            "Rain", "Drizzle","Thunderstorm","Snow"  ->binding.ivTemperatureIcon.setImageResource(R.drawable.rainy_weather)
            else -> binding.ivTemperatureIcon.setImageResource(R.drawable.sunny_weather)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUI(){
        binding.temperature = AppUtils.temperatureIndex.toString()
        binding.cityName = AppUtils.cityName
        binding.weatherDescription = AppUtils.description?.let { AppUtils.capitalizeEachWord(it) }
        binding.max = AppUtils.temperatureMax.toString()
        binding.min = AppUtils.temperatureMin.toString()

        val currentDate = AppUtils.getCurrentDate()
        binding.date = currentDate

        setWeatherImage(AppUtils.imageReference.toString())

    }

    private fun fetchWeatherPeriodically(min: Int) {
        val interval = min * 60 * 1000 // 15 minutes in milliseconds
        val timer = Timer()

        val task = object : TimerTask() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun run() {
                weatherUtils.setWeatherDetails()
                updateUI()
            }
        }

        timer.scheduleAtFixedRate (task , 0, interval.toLong())
    }


}



