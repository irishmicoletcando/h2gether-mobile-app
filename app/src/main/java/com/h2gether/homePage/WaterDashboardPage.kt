package com.h2gether.homePage

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.example.h2gether.R
import com.example.h2gether.databinding.FragmentWaterDashboardPageBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.PropertyName
import com.google.firebase.database.ValueEventListener
import com.h2gether.userAuthActivites.LoginActivity
import com.h2gether.userConfigActivities.WeightSelection

class WaterDashboardPage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var selectedOption: Int? = null
    private var targetWater: Int? = 2200
    private var waterConsumed: Int? = 0
    private var percent: Int? = 0

    private lateinit var binding: FragmentWaterDashboardPageBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWaterDashboardPageBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // fetch water details and other initializations
        fetchWaterDetails()

        binding.progressBar.max = 100

        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid

        databaseReference = FirebaseDatabase.getInstance().getReference("users/$uid/water-consumption")

        // button handlers
        binding.btnAddWater.setOnClickListener {
            if (waterConsumed!! < targetWater!!) {
                waterConsumed = selectedOption?.let { it1 -> waterConsumed?.plus(it1) }
                waterConsumed?.let { it1 -> setWaterDetails() }
                waterConsumed?.let { it1 ->
                    if (uid != null) {
                        saveWaterConsumption(it1)
                    }
                }
            }
        }

        binding.op50ml.setOnClickListener{
            selectedOption = 50
            binding.op50ml.setBackgroundResource(R.drawable.option_select_bg_pressed)
            binding.op100ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.op150ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.op200ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.op250ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.opCustom.setBackgroundResource(R.drawable.option_select_bg)
        }

        binding.op100ml.setOnClickListener{
            selectedOption = 100
            binding.op50ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.op100ml.setBackgroundResource(R.drawable.option_select_bg_pressed)
            binding.op150ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.op200ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.op250ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.opCustom.setBackgroundResource(R.drawable.option_select_bg)
        }

        binding.op150ml.setOnClickListener{
            selectedOption = 150
            binding.op50ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.op100ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.op150ml.setBackgroundResource(R.drawable.option_select_bg_pressed)
            binding.op200ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.op250ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.opCustom.setBackgroundResource(R.drawable.option_select_bg)
        }

        binding.op200ml.setOnClickListener{
            selectedOption = 200
            binding.op50ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.op100ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.op150ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.op200ml.setBackgroundResource(R.drawable.option_select_bg_pressed)
            binding.op250ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.opCustom.setBackgroundResource(R.drawable.option_select_bg)
        }

        binding.op250ml.setOnClickListener{
            selectedOption = 250
            binding.op50ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.op100ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.op150ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.op200ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.op250ml.setBackgroundResource(R.drawable.option_select_bg_pressed)
            binding.opCustom.setBackgroundResource(R.drawable.option_select_bg)
        }

        binding.opCustom.setOnClickListener {
            val promptsView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_water_input_dialog, null)
            val userInput = promptsView.findViewById(R.id.etCustomInput) as TextInputLayout

            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setView(promptsView)

            alertDialogBuilder.setPositiveButton("OK") { dialog, id ->
                val inputText = userInput.editText!!.text.toString()
                if (!TextUtils.isEmpty(inputText)) {
                    binding.tvCustom.text = "$inputText ml"
                    selectedOption = inputText.toInt()
                }
            }.setNegativeButton("Cancel") { dialog, id ->
                dialog.cancel()
            }

            // Add any additional customization or functionality to the dialog
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            binding.op50ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.op100ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.op150ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.op200ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.op250ml.setBackgroundResource(R.drawable.option_select_bg)
            binding.opCustom.setBackgroundResource(R.drawable.option_select_bg_pressed)
        }
    }

    private fun setWaterDetails(){
        binding.tvRecommendedAmount.text = targetWater.toString()
        binding.tvAmountConsumed.text = waterConsumed.toString()
        percent = (((waterConsumed?.toFloat()!!) / targetWater?.toFloat()!!) * 100).toInt()
        binding.progressBar.progress = percent!!
        binding.tvPercent.text = percent.toString() + "%"
    }

    private fun saveWaterConsumption(waterConsumed: Int){
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val existingData: Map<String, Any>? = snapshot.value as? Map<String, Any>

                    // Create a new map that includes the existing data and the new field
                    val newData = existingData?.toMutableMap() ?: mutableMapOf()
                    newData["waterConsumption"] = waterConsumed

                    databaseReference.updateChildren(newData)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun fetchWaterDetails(){
        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val dataRef: DatabaseReference = database.child("users/$uid/water-consumption")

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Handle the retrieved data here
                if (dataSnapshot.exists()) {
                    val waterConsumption = dataSnapshot.getValue(WaterConsumptionDataModel::class.java)
                    // Process the retrieved value as needed

                    if (waterConsumption != null) {
                        waterConsumed = waterConsumption.waterConsumption
                    }
                } else {
                    // Data does not exist at the specified location
                    waterConsumed = 0
                }
                Log.i(ContentValues.TAG, waterConsumed.toString())
                waterConsumed?.let { setWaterDetails() }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur during retrieval
            }
        }

        )


    }

    class WaterConsumptionDataModel {
        @PropertyName("waterConsumption")
        var waterConsumption: Int? = 0
    }
}