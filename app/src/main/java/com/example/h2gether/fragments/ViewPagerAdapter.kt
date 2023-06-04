package com.example.h2gether.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(list: ArrayList<Fragment>, fragmentManager: FragmentManager, lifecycle: Lifecycle):FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragmentList = list

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
//        return when(position){
//            0-> {
//                TrackWaterConsumptionPrefacePageFragment()
//            }
//            1-> {
//                FamilyMonitoringPrefacePageFragment()
//            }
//            2-> {
//                HydrationTipsPrefacePageFragment()
//            }
//            3-> {
//                CompetitionPrefacePageFragment()
//            }
//            else-> {
//                Fragment()
//            }
//        }
        return fragmentList[position]
    }
}