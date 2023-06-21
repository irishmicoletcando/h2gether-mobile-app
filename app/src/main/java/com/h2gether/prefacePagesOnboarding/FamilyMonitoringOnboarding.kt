package com.h2gether.prefacePagesOnboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.h2gether.R
import com.h2gether.userConfigActivities.SexSelectionActivity

class FamilyMonitoringOnboarding : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family_monitoring_onboarding)

        val btnBack = findViewById<Button>(R.id.btn_back)
        btnBack.setOnClickListener {
            val intent = Intent(this, TrackWaterConsumptionOnboarding::class.java)
            backActivityWithSlideAnimation(intent)
        }

        val btnNext = findViewById<Button>(R.id.btn_next)
        btnNext.setOnClickListener {
            val intent = Intent(this, HydrationTipsOnboarding::class.java)
            startActivityWithSlideAnimation(intent)
        }

        val btnSkip = findViewById<Button>(R.id.btn_skip)
        btnSkip.setOnClickListener {
            val intent = Intent(this, SexSelectionActivity::class.java)
            startActivityWithSlideAnimation(intent)
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