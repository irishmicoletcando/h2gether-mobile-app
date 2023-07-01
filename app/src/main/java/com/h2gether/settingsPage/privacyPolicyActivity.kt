package com.h2gether.settingsPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.h2gether.R

class privacyPolicyActivity : AppCompatActivity() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_settings_page, container, false)

        val backButton = rootView.findViewById<ImageButton>(R.id.back_button)
        val pageTitle = rootView.findViewById<TextView>(R.id.toolbar_title)
        val logoutButton: ImageButton = rootView.findViewById(R.id.logout_button)
        logoutButton.visibility = View.GONE

        // Customize the toolbar as needed
        pageTitle.text = "Privacy Policy"
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return rootView
    }
}