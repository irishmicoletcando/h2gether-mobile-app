package com.example.h2gether

import android.app.Activity
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

        binding.tvRegisterAccount.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignin.setOnClickListener{
            val email = binding.etInputEmail .text.toString()
            val pass = binding.etInputPassword.text.toString()


            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        // Call the function to handle "Remember Me" preference
                        handleRememberMe(user?.uid)
                        val intent = Intent(this, SexSelectionActivity ::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                    }
                }
            } else {
                Toast.makeText(this, "Empty fields are not allowed!", Toast.LENGTH_SHORT).show()

            }
        }

        loadRememberMeStatus()

        binding.btnGoogle.setOnClickListener{
            signInGoogle()
        }

    }

    private fun handleRememberMe(userId: String?) {
        val rememberMe = binding.cbRememberAccount.isChecked
        saveRememberMeStatus(rememberMe)

        if (rememberMe && userId != null) {
            // Remember Me is checked and user is logged in

            // Save user ID to Firebase Database
            val databaseReference = firebaseDatabase.getReference("users")
            databaseReference.child(userId).setValue(true)

            // Perform necessary actions like saving login credentials or other relevant tasks
        } else {
            // Remember Me is unchecked or user is not logged in

            // Clear login credentials or perform necessary actions

            // Clear the email and password fields
            binding.etInputEmail.setText("")
            binding.etInputPassword.setText("")

            // Sign out the user from Firebase Authentication
            firebaseAuth.signOut()

            // Delete the user ID from Firebase Database
            if (userId != null) {
                val databaseReference = firebaseDatabase.getReference("users")
                databaseReference.child(userId).removeValue()
            }
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
                val intent : Intent = Intent(this , SexSelectionActivity::class.java)
                intent.putExtra("email" , account.email)
                intent.putExtra("name" , account.displayName)
                startActivity(intent)
            }else{
                Toast.makeText(this, it.exception.toString() , Toast.LENGTH_SHORT).show()

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