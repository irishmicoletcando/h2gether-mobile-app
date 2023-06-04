package com.example.h2gether

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.h2gether.fragments.CompetitionPrefacePageFragment
import com.example.h2gether.fragments.FamilyMonitoringPrefacePageFragment
import com.example.h2gether.fragments.HydrationTipsPrefacePageFragment
import com.example.h2gether.fragments.TrackWaterConsumptionPrefacePageFragment
import com.example.h2gether.fragments.ViewPagerAdapter
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class PrefaceFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_preface, container, false)

        val fragmentList = arrayListOf<Fragment>(
            TrackWaterConsumptionPrefacePageFragment(),
            FamilyMonitoringPrefacePageFragment(),
            HydrationTipsPrefacePageFragment(),
            CompetitionPrefacePageFragment()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        val viewPager = view.findViewById<ViewPager2>(R.id.viewPagerTwo)

        viewPager.adapter = adapter
        val indicator = view.findViewById<DotsIndicator>(R.id.dots_indicator)
        indicator.attachTo(viewPager)

        return view
    }
}