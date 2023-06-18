package com.h2gether.homePage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.h2gether.R
import com.example.h2gether.databinding.ActivityNavigationBarBinding

class NavigationBarActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNavigationBarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(WaterDashboardPage())

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {

            when(it.itemId) {
                R.id.water_page -> replaceFragment(WaterDashboardPage())
                R.id.statistics_page -> replaceFragment(StatisticsPage())
                R.id.reminder_page -> replaceFragment(ReminderPage())
                R.id.tips_page -> replaceFragment(TipsPage())

                else -> {

                }
            }

            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.constraint_layout, fragment)
        fragmentTransaction.commit()
    }
}