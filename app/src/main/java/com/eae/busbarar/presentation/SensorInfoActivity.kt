package com.eae.busbarar.presentation

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.eae.busbarar.databinding.ActivitySensorInfoBinding
import com.eae.busbarar.data.model.TextRecognitionRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SensorInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySensorInfoBinding
    private val viewModel: PreviewViewModel by viewModels()
    private var temperature: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.sensorNameText.text = sensorId


        /*ArrayAdapter.createFromResource(
            this,
            com.eae.busbarar.R.array.temperature,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.spinner.adapter = adapter
        }//.array.temperature
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding.temperatureValue.text = resources.getStringArray(com.eae.busbarar.R.array.temperature)[p2]

                temperature = resources.getStringArray(com.eae.busbarar.R.array.temperature)[p2].toInt()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }*/

        binding.sendData.setOnClickListener(){
            viewModel.uploadSensorId(TextRecognitionRequest(sensorId, 5))
        }

        viewModel.sensorResponse.observe(this){ response ->
            response?.let {safeResponse ->
                for (item in safeResponse.temps?: listOf()){
                    item.datetime
                    item.pred
                    item.values
                }
                //Grafik Chart Setle
            } ?: run {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
            }
        }

    }

    fun setLineChartData(){

    }

    companion object {
        var sensorId: String? = null
    }


}