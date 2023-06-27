package com.h2gether.userConfigActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import com.example.h2gether.R
import com.example.h2gether.databinding.ActivityHeightSelectionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HeightSelection : AppCompatActivity() {
    private lateinit var binding: ActivityHeightSelectionBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private val INITIAL_HEIGHT = 150

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHeightSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid
        var heightValue = 1

        databaseReference = FirebaseDatabase.getInstance().getReference("users/$uid/user-profile")

        var seekBarHeight = binding.seekbarHeight
        var tvHeight = binding.tvHeight
        seekBarHeight.progress = INITIAL_HEIGHT
        tvHeight.text = INITIAL_HEIGHT.toString()

        seekBarHeight.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvHeight.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })


        binding.btnNext.setOnClickListener {
            if (uid != null) {
                heightValue = binding.seekbarHeight.progress

                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val existingData: Map<String, Any>? = snapshot.value as? Map<String, Any>

                        // Create a new map that includes the existing data and the new field
                        val newData = existingData?.toMutableMap() ?: mutableMapOf()
                        newData["height (cm)"] = heightValue

                        databaseReference.updateChildren(newData)
                            .addOnSuccessListener {
                                Toast.makeText(this@HeightSelection, "Height has been set.", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@HeightSelection, ActivityLevelSelection::class.java)
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
                Toast.makeText(this, "Failed to set height", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, WeightSelection::class.java)
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