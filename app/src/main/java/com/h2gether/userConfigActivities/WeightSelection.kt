package com.h2gether.userConfigActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import com.example.h2gether.R
import com.example.h2gether.databinding.ActivityWeightSelectionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class WeightSelection : AppCompatActivity() {
    private lateinit var binding: ActivityWeightSelectionBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private val INITIAL_WEIGHT = 50

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeightSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid
        var weightValue = 1

        databaseReference = FirebaseDatabase.getInstance().getReference("users/$uid/user-profile")

        var seekBarWeight = binding.seekbarWeight
        var tvWeight = binding.tvWeight
        seekBarWeight.progress = INITIAL_WEIGHT
        tvWeight.text = INITIAL_WEIGHT.toString()

        seekBarWeight.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvWeight.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })


        binding.btnNext.setOnClickListener {
            if (uid != null) {
                weightValue = binding.seekbarWeight.progress

                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val existingData: Map<String, Any>? = snapshot.value as? Map<String, Any>

                        // Create a new map that includes the existing data and the new field
                        val newData = existingData?.toMutableMap() ?: mutableMapOf()
                        newData["weight (kg)"] = weightValue

                        databaseReference.updateChildren(newData)
                            .addOnSuccessListener {
                                Toast.makeText(this@WeightSelection, "Weight has been set.", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@WeightSelection, HeightSelection::class.java)
                                startActivityWithSlideAnimation(intent)
                            }
                            .addOnFailureListener { error ->
                                // Handle the failure
                            }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle the cancellation
                    }
                })
            } else {
                Toast.makeText(this, "Failed to set weight", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, AgeSelection::class.java)
            backActivityWithSlideAnimation(intent)
        }
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