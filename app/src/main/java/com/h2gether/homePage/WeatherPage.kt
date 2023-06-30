package com.h2gether.homePage

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.h2gether.databinding.FragmentWeatherPageBinding
import com.h2gether.appUtils.WeatherUtils
import kotlinx.coroutines.runBlocking

class WeatherPage : Fragment() {
    private lateinit var binding: FragmentWeatherPageBinding
    private val weatherUtils = WeatherUtils()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentWeatherPageBinding.inflate(inflater, container, false)
        setToolBar("Weather")

        var weatherDetails = runBlocking {fetchWeatherDetails()}
        Log.i(ContentValues.TAG, "eto $weatherDetails")

        if (weatherDetails != null) {
            binding.weatherDetails = weatherDetails
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
}