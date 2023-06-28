package com.h2gether.homePage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.h2gether.R
import androidx.appcompat.widget.Toolbar
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import com.h2gether.userAuthActivites.LoginActivity

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
    private lateinit var username: TextView
    private lateinit var userProfilePicture: ImageView
    private lateinit var signOutButton: ImageButton

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

        username = rootView.findViewById(R.id.username)
        userSex = rootView.findViewById(R.id.user_sex)
        userAge = rootView.findViewById(R.id.user_age)
        userWeight = rootView.findViewById(R.id.user_weight)
        userHeight = rootView.findViewById(R.id.user_height)
        userLevel = rootView.findViewById(R.id.user_level)
        userLevelImage = rootView.findViewById<ImageView>(R.id.user_level_img)
        userProfilePicture = rootView.findViewById(R.id.profile_image_view)
        signOutButton = rootView.findViewById<ImageButton>(R.id.logout_button)

        //Sign Out
        signOutButton.setOnClickListener {
            signOutUser()
        }

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
                    userProfilePicture.setImageResource(when (userProfile.sex){
                        "Female" -> R.drawable.female_profile
                        "Male" -> R.drawable.male_profile
                        else -> R.drawable.profile
                    })
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

        currentUser?.let {
            val emailRef = database.getReference("users/$userId/email")
            emailRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val email = dataSnapshot.getValue(String::class.java)
                    val usernameText = currentUser.email?.substringBefore("@gmail.com")
                    username.text = usernameText
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors that occur during email retrieval
                }
            })
        }

        val toolbar = rootView.findViewById<Toolbar>(R.id.tool_bar)
        val backButton = rootView.findViewById<ImageButton>(R.id.back_button)
        val pageTitle = rootView.findViewById<TextView>(R.id.toolbar_title)

        // Customize the toolbar as needed
        pageTitle.text = "Profile"
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return rootView
    }
    private fun signOutUser() {
        auth.signOut()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)

        Toast.makeText(requireContext(), "Signed out successfully", Toast.LENGTH_SHORT).show()
        requireActivity().finish()
    }
}
