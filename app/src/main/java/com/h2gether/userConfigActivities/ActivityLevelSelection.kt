package com.h2gether.userConfigActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.h2gether.homePage.CreatingPlanActivity
import com.example.h2gether.R
import com.example.h2gether.databinding.ActivityLevelSelectionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ActivityLevelSelection : AppCompatActivity() {
    private lateinit var binding: ActivityLevelSelectionBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLevelSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid

        databaseReference = FirebaseDatabase.getInstance().getReference("users/$uid/user-profile")

        var activityLevelUserSelection: String? = null

        var selectedButton: Button? = null

        binding.btnLightlyActive.setOnClickListener{
            activityLevelUserSelection = "lightly active"
            selectedButton = binding.btnLightlyActive
            updateButtonState(selectedButton)
        }

        binding.btnModeratelyActive.setOnClickListener{
            activityLevelUserSelection = "Moderately active"
            selectedButton = binding.btnModeratelyActive
            updateButtonState(selectedButton)
        }

        binding.btnVeryActive.setOnClickListener{
            activityLevelUserSelection = "Very active"
            selectedButton = binding.btnVeryActive
            updateButtonState(selectedButton)
        }

        binding.btnSedentary.setOnClickListener{
            activityLevelUserSelection = "Sedentary"
            selectedButton = binding.btnSedentary
            updateButtonState(selectedButton)
        }

        binding.btnNext.setOnClickListener {
            if (activityLevelUserSelection != null) {
                if (uid != null) {
                    databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val existingData: Map<String, Any>? = snapshot.value as? Map<String, Any>

                            // Create a new map that includes the existing data and the new field
                            val newData = existingData?.toMutableMap() ?: mutableMapOf()
                            newData["activityLevel"] = activityLevelUserSelection!!

                            databaseReference.updateChildren(newData)
                                .addOnSuccessListener {
                                    //
                                }
                                .addOnFailureListener { error ->
                                    // Handle the failure
                                }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })

                    Toast.makeText(this,"Activity level has been set.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, CreatingPlanActivity::class.java)
                    startActivityWithSlideAnimation(intent)

                }
                else Toast.makeText(this, "Failed to set Activity Level", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Select activity level", Toast.LENGTH_SHORT).show()
            }

        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, HeightSelection::class.java)
            backActivityWithSlideAnimation(intent)
        }
    }
    private fun updateButtonState(button: Button?) {
        // Reset the background color and any other visual states for both buttons
        binding.btnLightlyActive.setBackgroundResource(R.drawable.config_buttons)
        binding.btnModeratelyActive.setBackgroundResource(R.drawable.config_buttons)
        binding.btnVeryActive.setBackgroundResource(R.drawable.config_buttons)
        binding.btnSedentary.setBackgroundResource(R.drawable.config_buttons)

        // Check if a button is selected and update its UI state accordingly
        button?.setBackgroundResource(R.drawable.user_config_button_pressed)
    }

    private fun startActivityWithSlideAnimation(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
    }

    private fun backActivityWithSlideAnimation(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
    }
}

