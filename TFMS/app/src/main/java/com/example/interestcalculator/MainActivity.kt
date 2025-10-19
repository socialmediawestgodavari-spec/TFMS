package com.example.interestcalculator

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.interestcalculator.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var date1: Date? = null
        var date2: Date? = null

        binding.btnDate1.setOnClickListener {
            showDatePicker { selectedDate ->
                binding.txtDate1.text = dateFormat.format(selectedDate)
                date1 = selectedDate
                updateDuration(date1, date2)
            }
        }

        binding.btnDate2.setOnClickListener {
            showDatePicker { selectedDate ->
                binding.txtDate2.text = dateFormat.format(selectedDate)
                date2 = selectedDate
                updateDuration(date1, date2)
            }
        }

        binding.btnCalculate.setOnClickListener {
            val amount = binding.edtAmount.text.toString().toDoubleOrNull() ?: 0.0
            val roi = binding.edtRate.text.toString().toDoubleOrNull() ?: 0.0

            if (date1 == null || date2 == null) {
                binding.txtResult.text = "Please select both dates."
                return@setOnClickListener
            }

            val daysBetween = ((date2!!.time - date1!!.time) / (1000 * 60 * 60 * 24)).toInt()
            if (daysBetween < 0) {
                binding.txtResult.text = "End date must be after start date."
                return@setOnClickListener
            }

            // Calculate Simple Interest where ROI = ₹ per ₹100 per month (≈30 days)
            val interest = (amount * roi * daysBetween) / (100 * 30)
            val total = amount + interest

            val duration = convertDays(daysBetween)

            binding.txtPrincipal.text = "Principal Amount: ₹${"%.2f".format(amount)}"
            binding.txtDuration.text =
                "Duration: ${duration["years"]} years, ${duration["months"]} months, ${duration["days"]} days"
            binding.txtTotal.text = "Total Amount (P + I): ₹${"%.2f".format(total)}"

            binding.txtResult.text =
                "Interest for $daysBetween days = ₹${"%.2f".format(interest)}"
        }
    }

    private fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val selected = Calendar.getInstance()
                selected.set(year, month, day)
                onDateSelected(selected.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun convertDays(days: Int): Map<String, Int> {
        val years = floor(days / 365.0).toInt()
        val months = floor((days % 365) / 30.0).toInt()
        val remainingDays = (days - (years * 365) - (months * 30))
        return mapOf("years" to years, "months" to months, "days" to remainingDays)
    }

    private fun updateDuration(date1: Date?, date2: Date?) {
        if (date1 == null || date2 == null) return
        val daysBetween = ((date2.time - date1.time) / (1000 * 60 * 60 * 24)).toInt()
        if (daysBetween >= 0) {
            val duration = convertDays(daysBetween)
            binding.txtDuration.text =
                "Duration: ${duration["years"]} years, ${duration["months"]} months, ${duration["days"]} days"
        }
    }
}
