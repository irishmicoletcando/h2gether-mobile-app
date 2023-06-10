package com.example.h2gether

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.h2gether.databinding.ActivityWeightSelectionBinding

class WeightSelection : AppCompatActivity() {
    private lateinit var binding: ActivityWeightSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeightSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.npWeight.minValue = 1
        binding.npWeight.maxValue = 300

        val btnNext = findViewById<Button>(R.id.btn_next)
        btnNext.setOnClickListener {
            val intent = Intent(this, HeightSelection::class.java)
            startActivity(intent)
        }

        val btnBack = findViewById<Button>(R.id.btn_back)
        btnBack.setOnClickListener {
            val intent = Intent(this, AgeSelection::class.java)
            startActivity(intent)
        }
    }
}