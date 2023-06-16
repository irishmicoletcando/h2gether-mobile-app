package com.example.h2gether

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView


class CreatingPlanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creating_plan)

        val creatingPlan = findViewById<TextView>(R.id.creatingPlan)

        // Set initial alpha to 0
        creatingPlan.alpha = 0f

        val fadeAnimation = ObjectAnimator.ofFloat(creatingPlan, "alpha", 0f, 1f)
        fadeAnimation.duration = 1000 // Animation duration in milliseconds
        fadeAnimation.interpolator = AccelerateDecelerateInterpolator()
        fadeAnimation.startDelay = 500 // Delay before animation starts in milliseconds
        fadeAnimation.start()

        val thread = Thread {
            try {
                Thread.sleep(4000)
                val intent = Intent(this, NavigationBarActivity::class.java)
                startActivity(intent)
                finish()
            } catch (_: Exception) {

            }
        }
        thread.start()
    }
}