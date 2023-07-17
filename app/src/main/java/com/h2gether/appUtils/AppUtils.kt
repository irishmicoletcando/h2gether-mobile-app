package com.h2gether.appUtils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class AppUtils private constructor(){

    // Water Variables
    var selectedOption: Int? = 0
    var targetWater: Int? = 5000
    var waterConsumed: Int? = 0
    var percent: Int? = 0
    var previousPercent: Int? = 0
    var isInitiallyOpened: Boolean = true

    // Weather Variables
    var temperatureIndex: Int? = 0
    var temperatureMin: Int? = 0
    var temperatureMax: Int? = 0
    var location: String? = ""
    var date: String? = ""
    var description: String? = ""
    var cityName: String? = ""
    var imageReference: String? = ""

    // User Configuration
    var age: Int? = 0
    var height: Int? = 0
    var weight: Int? = 0
    var sex: String? = ""
    var activityLevel: String? = ""

    // Timer
    val hour = 5
    val min = 1


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

    fun showToast(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun log(message: String){
        Log.i(ContentValues.TAG, message)
    }

}