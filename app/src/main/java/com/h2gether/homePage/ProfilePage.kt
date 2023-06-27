package com.h2gether.homePage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.h2gether.R
import androidx.appcompat.widget.Toolbar
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

//@IgnoreExtraProperties
//data class UserProfile(
//    val activityLevel: String? = "",
//    @get:PropertyName("age (yrs)") @set:PropertyName("age (yrs)")
//    var age: Int? = 0,
//
//    @get:PropertyName("height (cm)") @set:PropertyName("height (cm)")
//    var height: Int? = 0,
//    val sex: String? = "",
//
//    @get:PropertyName("weight (kg)") @set:PropertyName("weight (kg)")
//    var weight: Int? = 0,
//    )

class ProfilePage : Fragment() {
    // TODO: Rename and change types of parameters
    //Declare the views
    private lateinit var userSex: TextView
    private lateinit var userAge: TextView
    private lateinit var userWeight: TextView
    private lateinit var userHeight: TextView
    private lateinit var userLevel: TextView
    private lateinit var userLevelImage: ImageView

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

    // Declare the Firebase database reference
    private lateinit var userRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_profile_page, container, false)

        userSex = rootView.findViewById(R.id.user_sex)
        userAge = rootView.findViewById(R.id.user_age)
        userWeight = rootView.findViewById(R.id.user_weight)
        userHeight = rootView.findViewById(R.id.user_height)
        userLevel = rootView.findViewById(R.id.user_level)
        userLevelImage = rootView.findViewById<ImageView>(R.id.user_level_img)

        // Initialize the Firebase database reference
        auth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser? = auth.currentUser
        val userId = currentUser?.uid
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        userRef = database.getReference("users/$userId/user-profile")

        // Fetch user data from the database
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userProfile = dataSnapshot.getValue(UserProfile::class.java)
                userProfile?.let {
                    userSex.text = when (userProfile.sex){
                        "Female" -> "F"
                        "Male" -> "M"
                        else -> ""
                    }
                    userAge.text = userProfile.age?.toString() ?: ""
                    userWeight.text = userProfile.weight?.toString() ?: ""
                    userHeight.text = userProfile.height?.toString() ?: ""
                    userLevel.text = userProfile.activityLevel
                    userLevelImage.setImageResource(when (userProfile.activityLevel) {
                        "Sedentary" -> R.drawable.sedentary_icon
                        "Lightly active" -> R.drawable.lightly_active_icon
                        "Moderately active" -> R.drawable.moderately_active_icon
                        "Very active" -> R.drawable.very_active_icon
                        else -> R.drawable.moderately_active_icon
                    })
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur during data fetching
            }
})

        val toolbar = rootView.findViewById<Toolbar>(R.id.tool_bar)
        val backButton = rootView.findViewById<ImageButton>(R.id.back_button)
        val pageTitle = rootView.findViewById<TextView>(R.id.toolbar_title)

        // Customize the toolbar as needed
        pageTitle.text = "Profile"
        backButton.setOnClickListener {
            //TODO: Handle back button click
        }
        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfilePage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = ProfilePage()
    }
}
