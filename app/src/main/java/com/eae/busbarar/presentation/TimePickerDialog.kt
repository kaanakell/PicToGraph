package com.eae.busbarar.presentation

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import com.eae.busbarar.R
import com.eae.busbarar.data.model.Times
import com.eae.busbarar.databinding.DialogTimePickerBinding

class TimePickerDialog(
    context: Context
) : Dialog(context, R.style.DialogTheme){

    var listener : ((Times) -> Unit)? = null
    private val hours = arrayOf("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23")
    private val minutes = arrayOf("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
        "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DialogTimePickerBinding.inflate(LayoutInflater.from(context), null, false)

        setCancelable(true)
        setCanceledOnTouchOutside(true)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.attributes?.dimAmount = 0.8f
        window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        setContentView(binding.root)
        binding.pickHourEnd.displayedValues = hours
        binding.pickMinuteEnd.displayedValues = minutes

        binding.pickHour.minValue = 0
        binding.pickHour.maxValue = 23

        binding.pickHourEnd.minValue = 0
        binding.pickHourEnd.maxValue = 23

        binding.pickMinuteEnd.maxValue = 59
        binding.pickMinuteEnd.minValue = 0

        binding.pickMinute.maxValue = 59
        binding.pickMinute.minValue = 0

        binding.pickHour.displayedValues = hours
        binding.pickMinute.displayedValues = minutes



        binding.button.setOnClickListener {
            val startHour = binding.pickHour.value
            val endHour = binding.pickHourEnd.value
            val startMinute = binding.pickMinute.value
            val endMinute = binding.pickMinuteEnd.value
            val times = Times(startHour, startMinute, endHour, endMinute)
            listener?.invoke(times)
        }
    }

}