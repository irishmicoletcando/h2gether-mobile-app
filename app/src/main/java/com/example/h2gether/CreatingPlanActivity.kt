package com.example.h2gether

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class CreatingPlanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creating_plan)

        val thread = Thread {
            try {
                Thread.sleep(4000)
                val intent = Intent(this, NavigationBarActivity::class.java)
                startActivity(intent)
                finish()
            } catch (_: Exception) {

            }
        }
        thread.start()
    }
}