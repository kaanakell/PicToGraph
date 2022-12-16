package com.eae.busbarar.presentation

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import com.eae.busbarar.R
import com.eae.busbarar.data.model.EndDateTime
import com.eae.busbarar.databinding.DialogEndDateTimePickerBinding


class EndDateTimePickerDialog(
    context: Context
) : Dialog(context, R.style.DialogTheme) {
    var listener : ((EndDateTime) -> Unit)? = null
    private val hours = arrayOf("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23")
    private val minutes = arrayOf("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
        "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DialogEndDateTimePickerBinding.inflate(LayoutInflater.from(context), null, false)

        setCancelable(true)
        setCanceledOnTouchOutside(true)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.attributes?.dimAmount = 0.8f
        window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        setContentView(binding.root)

        binding.pickHourEnd.displayedValues = hours
        binding.pickMinuteEnd.displayedValues = minutes

        binding.pickHourEnd.minValue = 0
        binding.pickHourEnd.maxValue = 23

        binding.pickMinuteEnd.maxValue = 59
        binding.pickMinuteEnd.minValue = 0

        binding.button.setOnClickListener {

            val endMonth = if (binding.endDatePicker.month+1 < 10){
                "0${binding.endDatePicker.month+1}"
            }else{
                "${binding.endDatePicker.month+1}"
            }

            val endDay = if (binding.endDatePicker.dayOfMonth < 10){
                "0${binding.endDatePicker.dayOfMonth}"
            }else{
                "${binding.endDatePicker.dayOfMonth}"
            }

            val endHour = if (binding.pickHourEnd.value < 10){
                "0${binding.pickHourEnd.value}"
            }else{
                "${binding.pickHourEnd.value}"
            }

            val endMinute = if (binding.pickMinuteEnd.value < 10){
                "0${binding.pickMinuteEnd.value}"
            }else{
                "${binding.pickMinuteEnd.value}"
            }

            val endDate = "${binding.endDatePicker.year}-$endMonth-$endDay"
            val endTime = "$endHour:$endMinute:00"

            val endDateTime = EndDateTime(endDate, endTime)
            listener?.invoke(endDateTime)
        }
    }

}