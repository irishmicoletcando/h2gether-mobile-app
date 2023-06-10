package com.example.h2gether

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat

class SexSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sex_selection)

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

        val btnNext = findViewById<Button>(R.id.btn_next)
        btnNext.setOnClickListener {
            val intent = Intent(this, AgeSelection::class.java)
            startActivity(intent)
        }
    }

//    // Extension function to convert dp to pixels
//    private fun Int.dpToPx(): Int {
//        val density = resources.displayMetrics.density
//        return (this * density + 0.5f).toInt()
//    }


}
