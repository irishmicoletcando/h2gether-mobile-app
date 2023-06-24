package com.h2gether.splashScreen

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.example.h2gether.R
import com.h2gether.homePage.NavigationBarActivity
import com.h2gether.userAuthActivites.LoginActivity

class SplashScreen : AppCompatActivity() {

    private val splashDuration = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val fadeAnimations = AnimationUtils.loadAnimation(this, R.anim.fade_out_animation)
        findViewById<View>(android.R.id.content).startAnimation(fadeAnimations)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        },splashDuration)

    }

}