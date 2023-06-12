package com.h2gether.userConfigActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.h2gether.R
import com.example.h2gether.databinding.ActivityAgeSelectionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AgeSelection : AppCompatActivity() {

    private lateinit var binding: ActivityAgeSelectionBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgeSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid
        var ageValue = 0

        databaseReference = FirebaseDatabase.getInstance().getReference("users/user-profile")

        binding.npAge.minValue = 1
        binding.npAge.maxValue = 100

        binding.btnNext.setOnClickListener {
            if (uid != null) {
                val newData: Map<String, Any> = mapOf(
                    "age" to (ageValue)
                )

                val updates: MutableMap<String, Any> = HashMap()
                updates["user$uid"] = newData

                databaseReference.updateChildren(updates).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, AgeSelection::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Failed to set age", Toast.LENGTH_SHORT).show()
                    }
                }

            }

            val intent = Intent(this, WeightSelection::class.java)
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, SexSelectionActivity::class.java)
            startActivity(intent)
        }

        binding.npAge.setOnValueChangedListener { picker, oldVal, newVal ->
            ageValue = binding.npAge.value

        }
    }
}