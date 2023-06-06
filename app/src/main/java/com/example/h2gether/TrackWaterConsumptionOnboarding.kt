package com.example.h2gether

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class TrackWaterConsumptionOnboarding : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_water_consumption_onboarding)

        val btnNext = findViewById<Button>(R.id.btn_next)
        btnNext.setOnClickListener {
            val intent = Intent(this, FamilyMonitoringOnboarding::class.java)
            startActivity(intent)
        }

        val btnSkip = findViewById<Button>(R.id.btn_skip)
        btnSkip.setOnClickListener {
            val intent = Intent(this, SexSelectionActivity::class.java)
            startActivity(intent)
        }

    }
}