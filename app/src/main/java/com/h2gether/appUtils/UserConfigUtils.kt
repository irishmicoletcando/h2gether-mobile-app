package com.h2gether.appUtils

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

class UserConfigUtils {
    val AppUtils = com.h2gether.appUtils.AppUtils.getInstance()

    @IgnoreExtraProperties
    class UserProfile(
        val activityLevel: String? = "",
        @get:PropertyName("age (yrs)") @set:PropertyName("age (yrs)")
        var age: Int? = 0,

        @get:PropertyName("height (cm)") @set:PropertyName("height (cm)")
        var height: Int? = 0,
        val sex: String? = "",

        @get:PropertyName("weight (kg)") @set:PropertyName("weight (kg)")
        var weight: Int? = 0,
    )
}