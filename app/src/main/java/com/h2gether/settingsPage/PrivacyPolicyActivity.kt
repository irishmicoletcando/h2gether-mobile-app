package com.h2gether.settingsPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.h2gether.R
import com.example.h2gether.databinding.ActivityPrivacyPolicyBinding
import com.example.h2gether.databinding.ActivityToolBarBinding

class PrivacyPolicyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivacyPolicyBinding
    private lateinit var toolBarBinding: ActivityToolBarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolBarBinding = ActivityToolBarBinding.bind(binding.root.findViewById(R.id.toolbarPrivacyLayout))
        val pageTitle = toolBarBinding.toolbarTitle
        val logoutButton = toolBarBinding.logoutButton
        logoutButton.visibility = View.GONE

        // Customize the toolbar as needed
        pageTitle.text = "Privacy Policy"

        toolBarBinding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
    }
}