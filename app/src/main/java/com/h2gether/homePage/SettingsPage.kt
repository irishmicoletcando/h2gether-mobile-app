package com.h2gether.homePage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.h2gether.R
import com.example.h2gether.databinding.ActivityToolBarBinding
import com.example.h2gether.databinding.FragmentSettingsPageBinding
import com.h2gether.settingsPage.AboutUsActivity
import com.h2gether.settingsPage.PrivacyPolicyActivity

class SettingsPage : Fragment() {
    private lateinit var binding: FragmentSettingsPageBinding
    private lateinit var toolBarBinding: ActivityToolBarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsPageBinding.inflate(inflater, container, false)
        toolBarBinding = ActivityToolBarBinding.bind(binding.root.findViewById(R.id.toolbarSettingsLayout))

        val pageTitle = toolBarBinding.toolbarTitle
        val logoutButton = toolBarBinding.logoutButton
        logoutButton.visibility = View.GONE

        // Customize the toolbar as needed
        pageTitle.text = "Settings"

        // back button in toolbar
        toolBarBinding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.llPrivacyPolicy.setOnClickListener {
            val intent = Intent(requireActivity(), PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        binding.llAboutUs.setOnClickListener {
            val intent = Intent(requireActivity(), AboutUsActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }
}
