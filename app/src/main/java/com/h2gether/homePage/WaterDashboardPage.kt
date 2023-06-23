package com.h2gether.homePage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.example.h2gether.R
import com.example.h2gether.databinding.FragmentWaterDashboardPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.h2gether.userConfigActivities.WeightSelection

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WaterDashboardPage.newInstance] factory method to
 * create an instance of this fragment.
 */
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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

        binding.progressBar.max = 100

        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("users/$uid/water-consumption")

        binding.btnAddWater.setOnClickListener {
            waterConsumed = selectedOption?.let { it1 -> waterConsumed?.plus(it1) }
            waterConsumed?.let { it1 -> setWaterLevel(it1) }
            waterConsumed?.let { it1 ->
                if (uid != null) {
                    saveWaterConsumption(it1)
                }
            }
            Toast.makeText(context, "added $selectedOption ml, waterConsumed: $waterConsumed, percent: $percent", Toast.LENGTH_SHORT).show()
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


    }



    private fun setWaterLevel(waterConsumed: Int){
        binding.tvRecommendedAmount.text = targetWater.toString()
        binding.tvAmountConsumed.text = waterConsumed.toString()
        percent = ((waterConsumed.toFloat() / targetWater?.toFloat()!!) * 100).toInt()
        binding.progressBar.progress = percent!!
        binding.tvPercent.text = percent.toString() + "%"
    }

    private fun saveWaterConsumption(waterConsumed: Int){
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val existingData: Map<String, Any>? = snapshot.value as? Map<String, Any>

                    // Create a new map that includes the existing data and the new field
                    val newData = existingData?.toMutableMap() ?: mutableMapOf()
                    newData["water-consumed (ml)"] = waterConsumed

                    databaseReference.updateChildren(newData)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WaterDashboardPage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WaterDashboardPage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}