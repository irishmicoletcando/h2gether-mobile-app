package com.example.h2gether

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import android.animation.AnimatorSet


class CreatingPlanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creating_plan)

        val creatingPlan = findViewById<TextView>(R.id.creatingPlan)

        // Set initial alpha to 0
        creatingPlan.alpha = 0f

        val fadeInAnimation = ObjectAnimator.ofFloat(creatingPlan, "alpha", 0f, 1f)
        fadeInAnimation.duration = 1000 // Animation duration in milliseconds
        fadeInAnimation.interpolator = AccelerateDecelerateInterpolator()
        fadeInAnimation.startDelay = 500 // Delay before fade-in animation starts

        val fadeOutAnimation = ObjectAnimator.ofFloat(creatingPlan, "alpha", 1f, 0f)
        fadeOutAnimation.duration = 1000 // Animation duration in milliseconds
        fadeOutAnimation.interpolator = AccelerateDecelerateInterpolator()
        fadeOutAnimation.startDelay = 2000 // Delay before fade-out animation starts

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(fadeInAnimation, fadeOutAnimation)
        animatorSet.start()

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