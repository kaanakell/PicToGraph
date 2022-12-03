package com.eae.busbarar.presentation

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import com.eae.busbarar.R
import com.eae.busbarar.data.model.Dates
import com.eae.busbarar.databinding.DialogDatePickerBinding
import com.eae.busbarar.databinding.DialogTimePickerBinding

class DatePickerDialog(
    context: Context
) : Dialog(context, R.style.DialogTheme) {
    var listener : ((Dates) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DialogDatePickerBinding.inflate(LayoutInflater.from(context), null, false)

        setCancelable(true)
        setCanceledOnTouchOutside(true)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.attributes?.dimAmount = 0.8f
        window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        setContentView(binding.root)


        binding.button.setOnClickListener {
            val startMonth = if (binding.startDatePicker.month+1 < 10){
                "0${binding.startDatePicker.month+1}"
            }else{
                "${binding.startDatePicker.month+1}"
            }
            val endMonth = if (binding.endDatePicker.month+1 < 10){
                "0${binding.endDatePicker.month+1}"
            }else{
                "${binding.endDatePicker.month+1}"
            }
            val startDay = if (binding.startDatePicker.dayOfMonth < 10){
                "0${binding.startDatePicker.dayOfMonth}"
            }else{
                "${binding.startDatePicker.dayOfMonth}"
            }
            val endDay = if (binding.endDatePicker.dayOfMonth < 10){
                "0${binding.endDatePicker.dayOfMonth}"
            }else{
                "${binding.endDatePicker.dayOfMonth}"
            }
            val startDate = "${binding.startDatePicker.year}-$startMonth-$startDay"
            val endDate = "${binding.endDatePicker.year}-$endMonth-$endDay"
            val dates = Dates(startDate, endDate)
            listener?.invoke(dates)
        }
    }

}