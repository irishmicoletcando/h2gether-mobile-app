package com.h2gether.homePage

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.h2gether.R
import com.example.h2gether.databinding.ActivityToolBarBinding
import com.example.h2gether.databinding.FeedbackDialogLayoutBinding
import com.example.h2gether.databinding.FragmentSettingsPageBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.h2gether.settingsPage.AboutUsActivity
import com.h2gether.settingsPage.PrivacyPolicyActivity

class SettingsPage : Fragment() {
    private lateinit var binding: FragmentSettingsPageBinding
    private lateinit var toolBarBinding: ActivityToolBarBinding
    private lateinit var feedbackDialogBinding: FeedbackDialogLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsPageBinding.inflate(inflater, container, false)
        toolBarBinding = ActivityToolBarBinding.bind(binding.root.findViewById(R.id.toolbarSettingsLayout))
        feedbackDialogBinding = FeedbackDialogLayoutBinding.inflate(inflater, container, false)

        val pageTitle = toolBarBinding.toolbarTitle
        val logoutButton = toolBarBinding.logoutButton
        logoutButton.visibility = View.GONE

        // Customize the toolbar as needed
        pageTitle.text = "Settings"

        // back button in toolbar
        toolBarBinding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.llFeedback.setOnClickListener {
            val promptsView = LayoutInflater.from(requireContext())
                .inflate(R.layout.feedback_dialog_layout, null)
            val userInput = promptsView.findViewById(R.id.etFeedback) as TextInputLayout

            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setView(promptsView)

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            val feedbackDialogBinding = FeedbackDialogLayoutBinding.bind(promptsView)
            val username = feedbackDialogBinding.etNameInput
            val feedback = feedbackDialogBinding.etFeedbackInput

            feedbackDialogBinding.btnSubmit.setOnClickListener {
                val usernameInput = username.text?.toString()?.trim()
                val feedbackInput = feedback.text?.toString()?.trim()

                if (usernameInput.isNullOrEmpty()) {
                    username.error = "Please enter your name"
                    return@setOnClickListener
                }

                if (feedbackInput.isNullOrEmpty()) {
                    feedback.error = "Please enter your feedback"
                    return@setOnClickListener
                }

                val i = Intent(Intent.ACTION_SEND)
                i.type = "message/html"
                i.putExtra(Intent.EXTRA_EMAIL, arrayOf("skylarkh2@gmail.com"))
                i.putExtra(Intent.EXTRA_SUBJECT, "Feedback From App")
                i.putExtra(Intent.EXTRA_TEXT, "Name: ${username.text}\n\nFeedback: ${feedback.text}")
                try {
                    startActivity(Intent.createChooser(i, "Please select email"))
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(requireContext(), "There are no email clients", Toast.LENGTH_SHORT).show()
                }
            }
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
