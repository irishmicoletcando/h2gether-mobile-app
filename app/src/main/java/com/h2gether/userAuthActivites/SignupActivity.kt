package com.h2gether.userAuthActivites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.h2gether.prefacePagesOnboarding.TrackWaterConsumptionOnboarding
import com.example.h2gether.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

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
            } else if(email.isNotEmpty() && pass.isNotEmpty()) {
                if (confirmPass.isEmpty()) {
                    Toast.makeText(this, "Please confirm password", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(this, "Please enter email or password", Toast.LENGTH_SHORT).show()

            }

        }


        }
    // Function to validate email using regex pattern
    private fun isEmailValid(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        val isEmailValid = pattern.matcher(email).matches()
        val domain = email.substringAfterLast("@").lowercase()
        return isEmailValid && (domain == "gmail.com" || domain == "yahoo.com" || domain == "outlook.com" || domain == "icloud.com")
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
            Toast.makeText(this, "Password must be at least 8 characters and includes 1 letter and 1 number. Special characters are not allowed", Toast.LENGTH_LONG).show()
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
                // An error occurred during sign-in
                try {
                    throw it.exception!!
                } catch (e: FirebaseAuthException) {
                    val errorCode = e.errorCode
                    val errorMessage = getAlertMessage(errorCode)
                    showToast(errorMessage)
                } catch (e: Exception) {
                    showToast("Authentication failed")
                }
            }
        }
    }

    fun getAlertMessage(errorCode: String): String {
        return when (errorCode) {
            "ERROR_INVALID_EMAIL" -> "Please enter a valid email address"
            "ERROR_WRONG_PASSWORD" -> "Incorrect password. Please enter the correct password"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "An account with this email already exists"
            // Add more error codes and messages as needed
            else -> {
                val prefix = "Firebase: "
                val errorMessage = errorCode.removePrefix(prefix)
                "Authentication failed: $errorMessage"
            }
        }
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

