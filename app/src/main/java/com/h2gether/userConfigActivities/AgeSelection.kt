package com.h2gether.userConfigActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import com.example.h2gether.R
import com.example.h2gether.databinding.ActivityAgeSelectionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AgeSelection : AppCompatActivity() {

    private lateinit var binding: ActivityAgeSelectionBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private val INITIAL_AGE = 18

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgeSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid
        var ageValue = 1

        databaseReference = FirebaseDatabase.getInstance().getReference("users/$uid/user-profile")

        var seekBarAge = binding.seekbarAge
        var tvAge = binding.tvAge
        seekBarAge.progress = INITIAL_AGE
        tvAge.text = INITIAL_AGE.toString()
        seekBarAge.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvAge.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

//
//        ageValue = binding.seekBarAge.value

        binding.btnNext.setOnClickListener {
            if (uid != null) {
                ageValue = binding.seekbarAge.progress

                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val existingData: Map<String, Any>? = snapshot.value as? Map<String, Any>

                        // Create a new map that includes the existing data and the new field
                        val newData = existingData?.toMutableMap() ?: mutableMapOf()
                        newData["age (yrs)"] = ageValue

                        databaseReference.updateChildren(newData)
                            .addOnSuccessListener {
                                Toast.makeText(this@AgeSelection, "Age has been set.", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@AgeSelection, WeightSelection::class.java)
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
                Toast.makeText(this, "Failed to set age", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, SexSelectionActivity::class.java)
            backActivityWithSlideAnimation(intent)
        }

//        binding.seekbarAge.setOnSeekBarChangeListener() { picker, oldVal, newVal ->
//            ageValue = binding.npAge.value
//
//        }
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