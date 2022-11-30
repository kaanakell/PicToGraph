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

        }
    }

}