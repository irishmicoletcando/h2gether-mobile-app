package com.example.h2gether

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.h2gether.databinding.ActivityMainBinding
import com.example.h2gether.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.tvRegisterAccount.setOnClickListener{
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }

        binding.btnSignin.setOnClickListener{
            val email = binding.etInputEmail .text.toString()
            val pass = binding.etInputPassword.text.toString()


            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, PrefaceActivity ::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }

    }
//    override fun onStart() {
//        super.onStart()
//
//        if(firebaseAuth.currentUser != null){
//            val intent = Intent(this, PrefaceActivity::class.java)
//            startActivity(intent)
//        }
//    }
}