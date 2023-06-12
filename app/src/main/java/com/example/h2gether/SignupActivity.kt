package com.example.h2gether

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.h2gether.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        binding.btnSigninAccount.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignup.setOnClickListener{
            val email = binding.etInputEmail.text.toString()
            val pass = binding.etInputPassword.text.toString()
            val confirmPass = binding.etConfirmPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    validateInputs(email, pass)
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }

        }


        }
    // Function to validate email using regex pattern
    private fun isEmailValid(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    // Function to validate password
    private fun isPasswordValid(password: String): Boolean {
        val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"
        return password.matches(passwordRegex.toRegex())
    }

    // Function to display toast alert
    private fun showToastAlert(message: String) {
        // Replace `context` with your actual Android context reference
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Usage example
    private fun validateInputs(email: String, password: String) {
        if (!isEmailValid(email)) {
            showToastAlert("Invalid email address")
            return
        }

        if (!isPasswordValid(password)) {
            showToastAlert("Password must be at least 8 characters long and includes 1 letter and 1 number.")
            return
        }

        if (isPasswordValid(password) && isEmailValid(email)){
            createAccount(email, password)
            return
        }
    }

    private fun createAccount(email: String, password: String){
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(this, TrackWaterConsumptionOnboarding::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}

