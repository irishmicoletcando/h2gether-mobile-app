package com.h2gether.userAuthActivites

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.h2gether.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import android.content.Context
import android.util.Log
import com.h2gether.homePage.NavigationBarActivity
import com.example.h2gether.R
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.h2gether.homePage.WaterDashboardPage
import com.h2gether.userConfigActivities.SexSelectionActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences
    private val PREF_NAME = "RememberMePrefs"
    private val KEY_REMEMBER_ME = "rememberMe"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this , gso)

        loadRememberMe()

        binding.tvRegisterAccount.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignin.setOnClickListener{
            val email = binding.etInputEmail.text.toString()
            val pass = binding.etInputPassword.text.toString()


            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        val intent = Intent(this, NavigationBarActivity ::class.java)
                        startActivity(intent)
                        // Call the function to handle "Remember Me" preference
                        handleRememberMe(user?.uid)

                    } else {
                            try {
                                throw it.exception!!
                            } catch (e: FirebaseAuthException) {
                                val errorCode = e.errorCode
                                val errorMessage = when (errorCode) {
                                    "ERROR_INVALID_EMAIL" -> "Invalid email address"
                                    "ERROR_WRONG_PASSWORD" -> "Incorrect password. Please enter the correct password"
                                    "ERROR_USER_NOT_FOUND" -> "Account not found. Please check the email entered or create a new account."
                                    // Add more error codes and messages as needed
                                    else -> "Authentication failed: $errorCode"
                                }
                                showToast(errorMessage)
                            } catch (e: Exception) {
                                showToast("Authentication failed")
                            }
                        }
                    }
                }
             else {

            showToast("Please enter email or password.")
            }
        }

        loadRememberMeStatus()

        binding.btnGoogle.setOnClickListener{
            signInGoogle()
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun handleRememberMe(userId: String?) {
        val rememberMe = binding.cbRememberAccount.isChecked
        saveRememberMeStatus(rememberMe)
        val uid = firebaseAuth.currentUser?.uid

        if (rememberMe && userId != null) {
            // Remember Me is checked and user is logged in

            // Save user ID to Firebase Database
            val databaseReference = firebaseDatabase.getReference("users/$uid/log-in-credentials")
            data class User(val email: String, val password: String)
            val email = binding.etInputEmail.text.toString()
            val pass = binding.etInputPassword.text.toString()
            val newUser = User(email,pass)
            databaseReference.setValue(newUser)
            println("user remembered")



            // Perform necessary actions like saving login credentials or other relevant tasks
        } else {
            // Remember Me is unchecked or user is not logged in

            // Clear login credentials or perform necessary actions

            // Clear the email and password fields
            binding.etInputEmail.setText("")
            binding.etInputPassword.setText("")

            // Sign out the user from Firebase Authentication
//            firebaseAuth.signOut()
//
//            // Delete the user ID from Firebase Database
//            if (userId != null) {
//                val databaseReference = firebaseDatabase.getReference("users")
//                databaseReference.child(userId).removeValue()
//            }
        }
    }

    private fun saveRememberMeStatus(rememberMe: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_REMEMBER_ME, rememberMe)
        editor.apply()
    }

    private fun loadRememberMeStatus() {
        val rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)
        binding.cbRememberAccount.isChecked = rememberMe
    }
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }
        }else{
            Toast.makeText(this, task.exception.toString() , Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken , null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val isFirstSignIn = sharedPref.getBoolean("isFirstSignIn", true)

                if (isFirstSignIn) {
                    // It's the first sign-in
                    val intent : Intent = Intent(this , SexSelectionActivity::class.java)
                    intent.putExtra("email" , account.email)
                    intent.putExtra("name" , account.displayName)
                    startActivity(intent)

                    val editor = sharedPref.edit()
                    editor.putBoolean("isFirstSignIn", false)
                    editor.apply()
                } else {
                    val intent : Intent = Intent(this , NavigationBarActivity::class.java)
                    intent.putExtra("email" , account.email)
                    intent.putExtra("name" , account.displayName)
                    startActivity(intent)
                    // It's not the first sign-in
                }



            }else{
                Toast.makeText(this, it.exception.toString() , Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun loadRememberMe() {
    val uid = firebaseAuth.currentUser?.uid
    // Reference to the Firebase Realtime Database
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    // Read data from a specific location in the database
        val dataRef: DatabaseReference = database.child("users/$uid/log-in-credentials")

    // Add a ValueEventListener to retrieve the data
        dataRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)
            // Handle data change
            if (dataSnapshot.exists() && rememberMe) {
                val loginCredentials: YourDataModel? = dataSnapshot.getValue(YourDataModel::class.java)
                if (loginCredentials != null) {
                    Log.i(TAG, loginCredentials.toString())
                }
                if (loginCredentials != null) {
                    binding.etInputEmail.setText(loginCredentials?.email.toString())
                    binding.etInputPassword.setText(loginCredentials?.password.toString())

                }
            } else {
                binding.etInputEmail.setText("")
                binding.etInputPassword.setText("")
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Handle error
        }
    })
}

    class YourDataModel {
        var email: String? = null
        var password: String? = null
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
