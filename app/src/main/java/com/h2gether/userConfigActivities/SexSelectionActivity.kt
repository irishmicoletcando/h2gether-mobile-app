package com.h2gether.userConfigActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.example.h2gether.databinding.ActivitySexSelectionBinding
import com.google.firebase.database.FirebaseDatabase

class SexSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySexSelectionBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySexSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        val databaseReference = firebaseDatabase.getReference("users")
        val uid = firebaseAuth.currentUser?.uid

        var userSexSelection: String? = null
        val defaultValue = ""

        binding.btnMale.setOnClickListener {
            userSexSelection = "Male"
        }

        binding.btnFemale.setOnClickListener {
            userSexSelection = "Female"
        }



//        val btnMale = findViewById<Button>(R.id.btn_male)
//        val btnFemale = findViewById<Button>(R.id.btn_female)
//
//        val btnSelectedColor = ContextCompat.getColor(this, R.color.btnSelected)
//        val lightGrayColor = ContextCompat.getColor(this, R.color.lightGray)
//
//        btnMale.setOnClickListener {
//            val maleBackground = btnMale.background as GradientDrawable
//            if (btnMale.isSelected) {
//                maleBackground.setStroke(1.dpToPx(), btnSelectedColor)
//            } else {
//                maleBackground.setStroke(1.dpToPx(), lightGrayColor)
//            }
//            btnMale.isSelected = !btnMale.isSelected
//            return@setOnClickListener
//        }
//
//        btnFemale.setOnClickListener {
//            val femaleBackground = btnFemale.background as GradientDrawable
//            if (btnFemale.isSelected) {
//                femaleBackground.setStroke(1.dpToPx(), btnSelectedColor)
//            } else {
//                femaleBackground.setStroke(1.dpToPx(), lightGrayColor)
//            }
//            btnFemale.isSelected = !btnFemale.isSelected
//            return@setOnClickListener
//        }

        binding.btnNext.setOnClickListener {
            if (userSexSelection != null) {
                if (uid != null){
                    val newData: Map<String, Any> = mapOf(
                        "age" to (userSexSelection ?: defaultValue)
                    )

                    val updates: MutableMap<String, Any> = HashMap()
                    updates["user$uid"] = newData

                    databaseReference.updateChildren(updates).addOnCompleteListener{
                        if (it.isSuccessful){
                            val intent = Intent(this, AgeSelection::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this,"Failed to set gender", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this,"Please set gender", Toast.LENGTH_SHORT).show()
            }
        }

    }

//    // Extension function to convert dp to pixels
//    private fun Int.dpToPx(): Int {
//        val density = resources.displayMetrics.density
//        return (this * density + 0.5f).toInt()
//    }


}}
