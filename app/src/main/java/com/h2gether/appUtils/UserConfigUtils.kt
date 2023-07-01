package com.h2gether.appUtils

import com.example.h2gether.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import com.google.firebase.database.ValueEventListener
import com.h2gether.homePage.ProfilePage

class UserConfigUtils {
    lateinit var auth: FirebaseAuth
    val AppUtils = com.h2gether.appUtils.AppUtils.getInstance()

    fun setUserConfigurationDetails(callback: UserConfigCallback){
        auth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser? = auth.currentUser
        val uid = currentUser?.uid

        val database = FirebaseDatabase.getInstance()
        val userRef: DatabaseReference = database.getReference("users/$uid/user-profile")
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userProfile = dataSnapshot.getValue(UserConfigUtils.UserProfile::class.java)
                userProfile?.let {
                    AppUtils.sex = userProfile.sex.toString()
                    AppUtils.age = userProfile.age
                    AppUtils.weight = userProfile.weight
                    AppUtils.height = userProfile.height
                    AppUtils.activityLevel = userProfile.activityLevel.toString()
                    callback.onUserConfigFetched()
                }

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur during data fetching
            }
        })
    }

    interface UserConfigCallback {
        fun onUserConfigFetched()
    }

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