package com.h2gether.settingsPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.h2gether.R

class PrivacyPolicyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        val pageTitle = findViewById<TextView>(R.id.toolbar_title)
        val logoutButton: ImageButton = findViewById(R.id.logout_button)
        logoutButton.visibility = View.GONE

        // Customize the toolbar as needed
        pageTitle.text = "Privacy Policy"
        backButton.setOnClickListener {
            finish()
        }
    }
}