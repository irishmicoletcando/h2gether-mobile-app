package com.h2gether.userConfigActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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

        binding.btnLightlyActive.setOnClickListener{
            activityLevelUserSelection = "lightly active"
        }

        binding.btnModeratelyActive.setOnClickListener{
            activityLevelUserSelection = "Moderately active"
        }

        binding.btnVeryActive.setOnClickListener{
            activityLevelUserSelection = "Very active"
        }

        binding.btnSedentary.setOnClickListener{
            activityLevelUserSelection = "Sedentary"
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

                    val intent = Intent(this, AgeSelection::class.java)
                    startActivity(intent)

                }
                else Toast.makeText(this, "Failed to set Activity Level", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Select activity level", Toast.LENGTH_SHORT).show()
            }

        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, HeightSelection::class.java)
            startActivity(intent)
        }
    }
}