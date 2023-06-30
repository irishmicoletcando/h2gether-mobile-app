package com.h2gether.homePage

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.h2gether.databinding.FragmentWeatherPageBinding
import com.h2gether.appUtils.WeatherUtils
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WeatherPage : Fragment() {
    private lateinit var binding: FragmentWeatherPageBinding
    private val weatherUtils = WeatherUtils()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentWeatherPageBinding.inflate(inflater, container, false)
        setToolBar("Weather")

        var weatherDetails = runBlocking {fetchWeatherDetails()}

        if (weatherDetails != null) {

            // set values to text views
            binding.temperature = weatherDetails?.weatherData?.feels_like?.minus(
                273.15)!!.toInt().toString() + "°C"
            binding.weatherDetails = weatherDetails.cityName
            binding.weatherDescription = capitalizeEachWord(weatherDetails.weatherDetails[0].description)
            binding.max = weatherDetails?.weatherData?.temp_max?.minus(
                273.15)!!.toInt().toString()

            binding.min = weatherDetails?.weatherData?.temp_min?.minus(
                273.15)!!.toInt().toString()

            val currentDate = getCurrentDate()
            binding.date = currentDate

        }

        return binding.root
    }

    private fun setToolBar(title: String){
        binding.toolbarLayout.logoutButton.visibility = View.GONE
        binding.toolbarLayout.backButton.visibility = View.GONE
        binding.toolbarLayout.toolbarTitle.text = title
    }

    private suspend fun fetchWeatherDetails(): WeatherUtils.WeatherResponse? {
        val weatherUtils = WeatherUtils()
        return weatherUtils.getWeatherDetails()
    }

    private fun capitalizeEachWord(input: String): String {
        val words = input.split(" ")
        val capitalizedWords = words.map { it.capitalize() }
        return capitalizedWords.joinToString(" ")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        return currentDate.format(formatter)
    }

}