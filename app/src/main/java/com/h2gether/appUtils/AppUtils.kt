package com.h2gether.appUtils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class AppUtils private constructor(){

    // Water Variables
    var selectedOption: Int? = 0
    var targetWater: Int? = 2200
    var waterConsumed: Int? = 0
    var percent: Int? = 0
    var previousPercent: Int? = 0

    // Weather Variables
    var temperatureIndex: Int? = 0
    var temperatureMin: Int? = 0
    var temperatureMax: Int? = 0
    var location: String? = ""
    var date: String? = ""
    var description: String? = ""
    var cityName: String? = ""
    var imageReference: String? = ""

    companion object {
        @Volatile
        private var instance: AppUtils? = null

        fun getInstance(): AppUtils {
            return instance ?: synchronized(this) {
                instance ?: AppUtils().also { instance = it }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        return currentDate.format(formatter)
    }

    fun capitalizeEachWord(input: String): String {
        val words = input.split(" ")
        val capitalizedWords = words.map { it.capitalize() }
        return capitalizedWords.joinToString(" ")
    }


}