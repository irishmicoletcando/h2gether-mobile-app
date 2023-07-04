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
        addToList(
            "Set a daily goal",
            "Setting a daily water intake goal can help you drink more water. Simply the act of setting a goal can be motivating and make you more likely to make positive changes that last. It can also help to record your progress, which can keep you motivated to achieve your goal — and make it a habit.",
            R.drawable.analysis
        )

        addToList(
            "Replace other drinks with water",
            "One way to drink more water and boost your health and reduce your calorie intake  is to replace other drinks, such as soda and sports drinks, with water. These drinks are often full of added sugars, which can be extremely detrimental to your health.\n" +
                    "For optimal health, limit your added sugar intake to less than 5% of your calorie intake.",
            R.drawable.replace_other_drinks
        )

        addToList(
            "Drink one glass of water before each meal",
            "Another simple way to increase your water intake is to make a habit of drinking one glass of water before each meal.\n" +
                    "If you eat 3 meals per day, this adds an extra 3 cups (720 ml) to your daily water intake.",
            R.drawable.water_before_meal
        )
    }
}