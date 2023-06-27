package com.h2gether.homePage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.h2gether.R
import androidx.appcompat.widget.Toolbar
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfilePage : Fragment() {
    // TODO: Rename and change types of parameters

    // Declare the Firebase database reference
    private lateinit var userRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_profile_page, container, false)

        val toolbar = rootView.findViewById<Toolbar>(R.id.tool_bar)
        val backButton = rootView.findViewById<ImageButton>(R.id.back_button)
        val pageTitle = rootView.findViewById<TextView>(R.id.toolbar_title)

        // Customize the toolbar as needed
        pageTitle.text = "Profile"
        backButton.setOnClickListener {
            //TODO: Handle back button click
        }
        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfilePage.
         */
        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            ProfilePage().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
    }
}