package com.h2gether.userConfigActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.h2gether.R
import com.example.h2gether.databinding.ActivityHeightSelectionBinding

class HeightSelection : AppCompatActivity() {
    private lateinit var binding: ActivityHeightSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHeightSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.npHeight.minValue = 1
        binding.npHeight.maxValue = 300

        val btnNext = findViewById<Button>(R.id.btn_next)
        btnNext.setOnClickListener {
            val intent = Intent(this, ActivityLevelSelection::class.java)
            startActivity(intent)
        }

        val btnBack = findViewById<Button>(R.id.btn_back)
        btnBack.setOnClickListener {
            val intent = Intent(this, WeightSelection::class.java)
            startActivity(intent)
        }
    }
}