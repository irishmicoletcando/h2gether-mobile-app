package com.h2gether.prefacePagesOnboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.h2gether.R
import com.h2gether.userConfigActivities.SexSelectionActivity

class HydrationTipsOnboarding : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hydration_tips_onboarding)

        val btnBack = findViewById<Button>(R.id.btn_back)
        btnBack.setOnClickListener {
            val intent = Intent(this, FamilyMonitoringOnboarding::class.java)
            startActivity(intent)
        }

        val btnNext = findViewById<Button>(R.id.btn_next)
        btnNext.setOnClickListener {
            val intent = Intent(this, CompetitionOnboarding::class.java)
            startActivity(intent)
        }

        val btnSkip = findViewById<Button>(R.id.btn_skip)
        btnSkip.setOnClickListener {
            val intent = Intent(this, SexSelectionActivity::class.java)
            startActivity(intent)
        }
    }
}