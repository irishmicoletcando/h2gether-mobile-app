package com.h2gether.homePage

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.h2gether.R
import com.example.h2gether.databinding.ActivityToolBarBinding
import com.example.h2gether.databinding.FragmentStatisticsPageBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.PropertyName
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StatisticsPage : Fragment() {

    private lateinit var binding: FragmentStatisticsPageBinding
    private lateinit var toolBarBinding: ActivityToolBarBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private var waterConsumed: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStatisticsPageBinding.inflate(inflater, container, false)
        toolBarBinding = ActivityToolBarBinding.bind(binding.root.findViewById(R.id.toolbarLayout))

        val backButton = toolBarBinding.backButton
        val pageTitle = toolBarBinding.toolbarTitle
        val logoutButton = toolBarBinding.logoutButton

        logoutButton.visibility = View.GONE
        backButton.visibility = View.GONE

        // Customize the toolbar as needed
        pageTitle.text = "Statistics"

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchWaterDetails()
    }

    private fun fetchWaterDetails() {
        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val dataRef: DatabaseReference = database.child("users/$uid/water-consumption")

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val waterConsumption = dataSnapshot.getValue(WaterConsumptionDataModel::class.java)

                    if (waterConsumption != null) {
                        waterConsumed = waterConsumption.waterConsumption ?: 0

                        // Process the retrieved data and create the entries and dates
                        val entries = mutableListOf<Entry>()
                        val dates = mutableListOf<String>()

                        // Assuming waterConsumption is the value obtained from Firebase
                        if (waterConsumption != null) {
                            val calendar = Calendar.getInstance()
                            calendar.time = Date()
                            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY) // Start from Sunday

                            val sdf = SimpleDateFormat("MMMM dd", Locale.getDefault())

                            for (i in 0 until 7) {
                                val date = sdf.format(calendar.time)
                                val waterValue = waterConsumption.waterConsumption ?: 0
                                entries.add(Entry(i.toFloat(), waterValue.toFloat()))
                                dates.add(date)

                                calendar.add(Calendar.DAY_OF_WEEK, 1)
                            }

                            // Call the updateChart function with the processed data
                            updateChart(entries, dates)
                        }
                    }
                } else {
                    Toast.makeText(context, "No data available", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur during retrieval
            }
        })
    }


    private fun updateChart(entries: List<Entry>, dates: List<String>) {
        val chart = binding.lineChart

        // Create a data set from the entries
        val dataSet = LineDataSet(entries, "Water Intake")
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.black)
        dataSet.setCircleColor(ContextCompat.getColor(requireContext(), R.color.black))
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setDrawCircleHole(false)
        dataSet.valueTextSize = 10f
        dataSet.setDrawFilled(true)
        dataSet.fillColor = ContextCompat.getColor(requireContext(), R.color.azure)
        dataSet.fillAlpha = 50
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        // Create a LineData object from the data set
        val lineData = LineData(dataSet)

        // Customize the appearance of the line chart
        chart.description.isEnabled = false
        chart.setTouchEnabled(false)
        chart.isDragEnabled = false
        chart.setScaleEnabled(false)
        chart.setPinchZoom(false)

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.valueFormatter = object : IndexAxisValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                if (index >= 0 && index < dates.size) {
                    val date = dates[index]
                    val dayOfWeek = getDayOfWeek(date)
                    return "$dayOfWeek"
//                     return "$dayOfWeek $date"
                }
                return ""
            }
        }

        val yAxisLeft = chart.axisLeft
        yAxisLeft.axisMinimum = 0f
        yAxisLeft.axisMaximum = 5100f // Set the maximum value to 5100 mL
        yAxisLeft.setDrawLimitLinesBehindData(true)

        val yAxisRight = chart.axisRight
        yAxisRight.isEnabled = false

        // Set the LineData object to the chart and refresh the view
        chart.data = lineData
        chart.invalidate()

        // Update the date range text view
        val dateRangeTextView = binding.dateRangeTextView
        val startDate = dates.firstOrNull()
        val endDate = dates.lastOrNull()
        val dateRangeText = "$startDate - $endDate"
        dateRangeTextView.text = dateRangeText

        // Set visibility of values on the line chart based on the current date
        val currentDate = SimpleDateFormat("MMMM dd", Locale.getDefault()).format(Date())
        val currentDayIndex = dates.indexOf(currentDate)
        dataSet.setDrawValues { entry ->
            val entryIndex = entry.x.toInt()
            entryIndex == currentDayIndex
        }

        // Refresh the chart view after updating the draw values
        chart.notifyDataSetChanged()
        chart.invalidate()
    }

    class WaterConsumptionDataModel {
        @PropertyName("waterConsumption")
        var waterConsumption: Int? = 0
    }

    private fun LineDataSet.setDrawValues(condition: (Entry) -> Boolean) {
        setDrawValues(true)
        valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (condition(Entry(0f, value))) {
                    value.toInt().toString()
                } else {
                    ""
                }
            }
        }
    }

    private fun getDayOfWeek(dateString: String): String {
        val format = SimpleDateFormat("MMMM dd", Locale.US)
        val date = format.parse(dateString)
        val calendar = Calendar.getInstance().apply {
            time = date
        }
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val dayOfWeekFormat = SimpleDateFormat("EEE", Locale("en", "PH")) // Use Philippine locale for day of week
        val daysOfWeek = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        return daysOfWeek[(dayOfWeek + 2) % 7] // Adjust the day of week index to start from Sunday
    }
}
