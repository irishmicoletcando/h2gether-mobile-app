package com.example.h2gether

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.h2gether.fragments.CompetitionPrefacePageFragment
import com.example.h2gether.fragments.FamilyMonitoringPrefacePageFragment
import com.example.h2gether.fragments.HydrationTipsPrefacePageFragment
import com.example.h2gether.fragments.TrackWaterConsumptionPrefacePageFragment
import com.example.h2gether.fragments.ViewPagerAdapter
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class PrefaceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preface)

        val fragmentList = arrayListOf<Fragment>(
            TrackWaterConsumptionPrefacePageFragment(),
            FamilyMonitoringPrefacePageFragment(),
            HydrationTipsPrefacePageFragment(),
            CompetitionPrefacePageFragment()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            supportFragmentManager,
            lifecycle
        )

        val viewPager = findViewById<ViewPager2>(R.id.viewPagerTwo)

        viewPager.adapter = adapter
        val indicator = findViewById<DotsIndicator>(R.id.dots_indicator)
        indicator.attachTo(viewPager)
    }
}