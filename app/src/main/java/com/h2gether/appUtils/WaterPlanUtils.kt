package com.h2gether.appUtils

class WaterPlanUtils {
    val AppUtils = com.h2gether.appUtils.AppUtils.getInstance()
    val WeatherUtils = WeatherUtils()
    val UserConfigUtils = UserConfigUtils()


    fun setTargetWater() {
        AppUtils.targetWater = AppUtils.age?.let {
            AppUtils.temperatureIndex?.let { it1 ->
                calculateTargetWater(AppUtils.sex.toString(),
                    it, it1
                )
            }
        }
    }

    private fun calculateTargetWater(gender: String, age: Int, temperatureIndexCelsius: Int): Int {
        return when (gender) {
            "Female" -> {
                when {
                    age in 0..3 -> if (isModerateWeather(temperatureIndexCelsius)) 900 else 1700
                    age in 4..8 -> if (isModerateWeather(temperatureIndexCelsius)) 1200 else 2100
                    age in 9..13 -> if (isModerateWeather(temperatureIndexCelsius)) 1600 else 2400
                    age in 14..18 -> if (isModerateWeather(temperatureIndexCelsius)) 1800 else 3100
                    age in 19..30 -> if (isModerateWeather(temperatureIndexCelsius)) 2200 else 3700
                    age in 31..50 -> if (isModerateWeather(temperatureIndexCelsius)) 2200 else 3700
                    age >= 51 -> if (isModerateWeather(temperatureIndexCelsius)) 2200 else 3700
                    else -> -1 // Invalid age
                }
            }
            "Male" -> {
                when {
                    age in 0..3 -> if (isModerateWeather(temperatureIndexCelsius)) 900 else 1700
                    age in 4..8 -> if (isModerateWeather(temperatureIndexCelsius)) 1200 else 2100
                    age in 9..13 -> if (isModerateWeather(temperatureIndexCelsius)) 1800 else 2600
                    age in 14..18 -> if (isModerateWeather(temperatureIndexCelsius)) 2600 else 4500
                    age in 19..30 -> if (isModerateWeather(temperatureIndexCelsius)) 3000 else 5100
                    age in 31..50 -> if (isModerateWeather(temperatureIndexCelsius)) 3000 else 5100
                    age >= 51 -> if (isModerateWeather(temperatureIndexCelsius)) 3000 else 5100
                    else -> -1 // Invalid age
                }
            }
            else -> 2200
        }
    }

    private fun isModerateWeather(temperatureIndexCelsius: Int): Boolean {
        return temperatureIndexCelsius in 0..31
    }

}