package com.h2gether.homePage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.example.h2gether.databinding.FragmentWaterDashboardPageBinding

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

    private lateinit var binding: FragmentWaterDashboardPageBinding

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
    ): View? {
        binding = FragmentWaterDashboardPageBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddWater.setOnClickListener {
            Toast.makeText(context, "added $selectedOption ml", Toast.LENGTH_SHORT).show()
        }

        binding.op50ml.setOnClickListener{
            selectedOption = 50
//            binding.op50ml.background = getDrawable(outValue.resourceId)
//            binding.op100ml.background = getDrawable(R.drawable.option_select_bg)
//            binding.op150ml.background = getDrawable(outValue.resourceId)
//            binding.op200ml.background = getDrawable(outValue.resourceId)
//            binding.op250ml.background = getDrawable(outValue.resourceId)
//            binding.opCustom.background = getDrawable(outValue.resourceId)
        }

        binding.op100ml.setOnClickListener{
            selectedOption = 100
        }

        binding.op150ml.setOnClickListener{
            selectedOption = 150
        }

        binding.op200ml.setOnClickListener{
            selectedOption = 200
        }

        binding.op250ml.setOnClickListener{
            selectedOption = 250
        }


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