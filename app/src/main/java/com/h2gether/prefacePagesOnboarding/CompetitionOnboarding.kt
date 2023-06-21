package com.h2gether.prefacePagesOnboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.h2gether.R
import com.h2gether.userConfigActivities.SexSelectionActivity

class CompetitionOnboarding : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_competition_onboarding)

        val btnBack = findViewById<Button>(R.id.btn_back)
        btnBack.setOnClickListener {
            val intent = Intent(this, HydrationTipsOnboarding::class.java)
            startActivity(intent)
        }

        val btnGetStarted = findViewById<Button>(R.id.btn_get_started)
        btnGetStarted.setOnClickListener {
            val intent = Intent(this, SexSelectionActivity::class.java)
            startActivityWithSlideAnimation(intent)
        }
    }

    private fun startActivityWithSlideAnimation(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
    }
}