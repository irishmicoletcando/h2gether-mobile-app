package com.h2gether.homePage

import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StatisticsPage : Fragment() {

    private lateinit var binding: FragmentStatisticsPageBinding
    private lateinit var toolBarBinding: ActivityToolBarBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var statisticsReference: DatabaseReference
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

    data class WaterIntakeEntry(val date: String, var waterIntake: Float)

    private fun fetchWaterDetails() {
        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid
        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("users/$uid")

        // Retrieve the water consumption value from the "water-consumption" node
        fetchWaterConsumption(database) { waterConsumption ->
            // Create a water intake entry with the retrieved water consumption value
            val currentEntry = WaterIntakeEntry(getCurrentDate(), waterConsumption.toFloat())

            // Retrieve the statistics data for the past week from the "statistics" node
            fetchStatisticsData(database) { entries ->
                // Add the current entry to the list if the date doesn't already exist
                val currentDate = getCurrentDate()
                val existingEntry = entries.find { it.date == currentDate }
                if (existingEntry != null) {
                    // Update the water intake value for the existing entry
                    existingEntry.waterIntake = waterConsumption.toFloat()
                } else {
                    // Add the current entry to the list
                    entries.add(0, currentEntry)
                }

                // Sort the entries chronologically based on the date
                val sortedEntries = entries.sortedBy { it.date }

                // Extract the sorted dates and water intake values from the entries
                val dates = sortedEntries.map { it.date }
                val waterIntakeValues = sortedEntries.map { it.waterIntake }

                // Update the chart with the sorted dates and water intake values
                val chartEntries = waterIntakeValues.mapIndexed { index, value ->
                    Entry(index.toFloat(), value)
                }
                updateChart(chartEntries, dates)
            }
        }
    }

    private fun fetchWaterConsumption(database: DatabaseReference, onSuccess: (Int) -> Unit) {
        val databaseRef: DatabaseReference = database.child("water-consumption")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get the water consumption value from the snapshot, or default to 0 if not found
                val waterConsumption = dataSnapshot.getValue(WaterConsumptionDataModel::class.java)?.waterConsumption ?: 0
                // Log.d("Debug", "Water consumption: $waterConsumption")
                waterConsumed = waterConsumption

                onSuccess(waterConsumption)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur during retrieval
            }
        })
    }

    private fun fetchStatisticsData(database: DatabaseReference, onSuccess: (MutableList<WaterIntakeEntry>) -> Unit) {
        // Retrieve the water consumption value for the current date
        fetchWaterConsumption(database) { currentWaterConsumption ->
            val statisticsRef: DatabaseReference = database.child("statistics")

            statisticsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val currentDate = getCurrentDate()
                    val sdf = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
                    val calendar = Calendar.getInstance()
                    calendar.time = Date()
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY) // Start from Sunday

                    // Move the calendar back by 7 days to get the first day of the week
                    calendar.add(Calendar.DAY_OF_WEEK, -6)

                    val entries = mutableListOf<WaterIntakeEntry>()

                    // Iterate over the days of the week (Sunday to Saturday)
                    for (i in 0 until 7) {
                        val date = sdf.format(calendar.time)
                        val snapshot = dataSnapshot.child(date)

                        // Print the complete snapshot for debugging
                        // Log.d("FirebaseDebug", "Snapshot for $date: $snapshot")
                        // Log.d("FirebaseDebug", "Value for $date: ${snapshot.value}")

                        // Check if the snapshot exists and contains the water consumption value
                        val waterValue = if (date == formatDate(currentDate)) {
                            // Use the current date's water consumption value
                            currentWaterConsumption
                        } else {
                            // Retrieve the water consumption value from the snapshot, or default to 0 if the value is null
                            val value = snapshot.getValue(Long::class.java)
                            val waterValue = value?.toInt() ?: 0
                            // Log.d("FirebaseDebug", "Retrieved value for $date: $value")
                            waterValue
                        }

                        // Log the retrieved value for debugging
                        // Log.d("FirebaseDebug", "Water value for $date: $waterValue")

                        // Create a water intake entry with the retrieved water consumption value
                        val entry = WaterIntakeEntry(formatDate(date), waterValue.toFloat())
                        entries.add(entry)

                        calendar.add(Calendar.DAY_OF_WEEK, 1) // Move to the next day
                    }

                    // Sort the entries chronologically based on the date
                    val sortedEntries = entries.sortedBy { it.date }

                    onSuccess(sortedEntries.toMutableList())

                    // Log the retrieved data
                    for (entry in sortedEntries) {
                        Log.d("StatisticsData", "Date: ${entry.date}, Water Intake: ${entry.waterIntake}")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors that occur during retrieval
                    Log.e("StatisticsData", "Error retrieving statistics data: ${databaseError.message}")
                }
            })
        }
    }

    private fun formatDate(date: String): String {
        val monthNumber = date.substring(0, 2)
        val day = date.substring(3, 5)
        val month = when (monthNumber) {
            "01" -> "January"
            "02" -> "February"
            "03" -> "March"
            "04" -> "April"
            "05" -> "May"
            "06" -> "June"
            "07" -> "July"
            "08" -> "August"
            "09" -> "September"
            "10" -> "October"
            "11" -> "November"
            "12" -> "December"
            else -> ""
        }
        return "$month $day"
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("MMMM dd", Locale.getDefault())
        val currentDate = Date()
        return sdf.format(currentDate)
    }

    private fun updateChart(entries: List<Entry>, dates: List<String>) {
        if (!isAdded || !isResumed) {
            // Fragment is not attached to the activity or not in the resumed state.
            // Do not update the chart in this case.
            return
        }

        // Update the line chart with the retrieved data
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

        // Customize the x-axis of the line chart
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
                }
                return ""
            }
        }

        // Customize the y-axis of the line chart
        val yAxisLeft = chart.axisLeft
        yAxisLeft.axisMinimum = 0f
        yAxisLeft.axisMaximum = 5100f // Set the maximum value to 5100 mL
        yAxisLeft.setDrawLimitLinesBehindData(true)

        // Disable the right y-axis of the line chart
        val yAxisRight = chart.axisRight
        yAxisRight.isEnabled = false

        // Set the LineData object to the chart and refresh the view
        chart.data = lineData
//        chart.invalidate()

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

    private fun LineDataSet.setDrawValues(condition: (Entry) -> Boolean) {
        // Set whether to draw values on the line chart based on the given condition
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
        return try {
            val date = format.parse(dateString)
            val calendar = Calendar.getInstance().apply {
                time = date
            }
            val dayOfWeekFormat = SimpleDateFormat("EEE", Locale("en", "PH")) // Use Philippine locale for day of the week
            val daysOfWeek = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            daysOfWeek[(dayOfWeek + 2) % 7] // Adjust the day of the week index to start from Sunday
        } catch (e: ParseException) {
            Log.e("Error", "Error parsing date: $dateString")
            ""
        }
    }


    class WaterConsumptionDataModel {
        @PropertyName("waterConsumption")
        var waterConsumption: Int? = 0
    }

    class StatisticsDataModel {
        @PropertyName("statistics")
        var date: String = ""
        var waterConsumption: Int = 0
    }
}

