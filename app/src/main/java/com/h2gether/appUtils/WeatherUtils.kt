package com.h2gether.appUtils

import android.content.ContentValues
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


class WeatherUtils {
    val AppUtils = com.h2gether.appUtils.AppUtils.getInstance()
    @RequiresApi(Build.VERSION_CODES.O)
    fun setWeatherDetails(){
        var weatherDetails = runBlocking { getWeatherDetails() }
        AppUtils.temperatureIndex = weatherDetails?.weatherData?.feels_like?.minus(
            273.15
        )!!.toInt()
        // set values to text views
        AppUtils.cityName = weatherDetails.cityName
        AppUtils.description = AppUtils.capitalizeEachWord(weatherDetails.weatherDetails[0].description)
        AppUtils.temperatureMax = weatherDetails?.weatherData?.temp_max?.minus(
            273.15)!!.toInt()
        AppUtils.temperatureMin = weatherDetails?.weatherData?.temp_min?.minus(
            273.15)!!.toInt()
        AppUtils.imageReference = weatherDetails.weatherDetails[0].main

        @RequiresApi(Build.VERSION_CODES.O)
        AppUtils.date =  AppUtils.getCurrentDate()
    }

    suspend fun getWeatherDetails(): WeatherResponse? {
        return coroutineScope {
            val weatherDeferred = async { fetchWeather() }
            val weatherResponse = weatherDeferred.await()

            return@coroutineScope weatherResponse
        }
    }

    private suspend fun fetchWeather(): WeatherResponse? = withContext(Dispatchers.IO) {
        return@withContext fetchWeatherFromOpenWeather()
    }

    private suspend fun fetchWeatherFromOpenWeather(): WeatherResponse? {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val client = OpenWeatherMapApiClient(retrofit.create(OpenWeatherMapService::class.java))

        return client.getCurrentWeather("Manila")
    }

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
        var feels_like: Double,
        @SerializedName("temp_min")
        var temp_min: Double,
        @SerializedName("temp_max")
        var temp_max: Double
    )

    data class WeatherDetails(
        @SerializedName("main")
        val main: String,
        @SerializedName("description")
        val description: String,
    )

}