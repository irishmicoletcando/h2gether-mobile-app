package com.h2gether.appUtils

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
    var cityName: String? = ""

    companion object {
        @Volatile
        private var instance: AppUtils? = null

        fun getInstance(): AppUtils {
            return instance ?: synchronized(this) {
                instance ?: AppUtils().also { instance = it }
            }
        }
    }


}