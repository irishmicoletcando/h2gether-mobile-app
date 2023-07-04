package com.h2gether.homePage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.example.h2gether.R

class TipsPage : Fragment() {

    private val titlesList = mutableListOf<String>()
    private val descsList = mutableListOf<String>()
    private val imagesList = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_tips_page, container, false)

        val backButton = rootView.findViewById<ImageButton>(R.id.back_button)
        val pageTitle = rootView.findViewById<TextView>(R.id.toolbar_title)
        val logoutButton: ImageButton = rootView.findViewById(R.id.logout_button)
        logoutButton.visibility = View.GONE
        backButton.visibility = View.GONE
        // Customize the toolbar as needed
        pageTitle.text = "Hydration Tips"
//        backButton.setOnClickListener {
//            //TODO: Handle back button click
//        }
        val viewPager2 = rootView.findViewById<ViewPager2>(R.id.viewPager2)
        viewPager2.adapter = ViewPageAdapter(titlesList, descsList, imagesList)
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        postToList()

        return rootView
    }

    private fun addToList(title: String, description: String, image: Int) {
        titlesList.add(title)
        descsList.add(description)
        imagesList.add(image)
    }

    private fun postToList() {
        addToList(
            "Understand your fluid needs",
            "A common recommendation for daily water intake is 8 cups, but this is not based on science. NAM recommends that men consume 125 ounces (3,700 ml) and women about 90 ounces (2,700 ml) of fluid per day, including the fluid from water, other drinks, and foods",
            R.drawable.fluid_needs
        )
    }
}