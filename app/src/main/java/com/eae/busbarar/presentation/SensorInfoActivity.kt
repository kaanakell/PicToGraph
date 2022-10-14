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
import android.R
import android.content.Intent
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aatools.AAColor
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


        ArrayAdapter.createFromResource(
            this,
            com.eae.busbarar.R.array.temperature,
            R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
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
        }

        binding.sendData.setOnClickListener(){
            viewModel.uploadSensorId(TextRecognitionRequest(sensorId, 5))
        }

        viewModel.sensorResponse.observe(this){ response ->
            response?.let {safeResponse ->
                val values = arrayListOf<Int>()
                val dates = arrayListOf<String>()
                for (item in safeResponse.temps?: listOf()){
                    item.datetime
                    item.pred
                    item.value

                    item.value?.let{values.add(it)}
                    item.datetime?.let{dates.add(it)}
                }
                val aaChartModel : AAChartModel = AAChartModel()
                    .chartType(AAChartType.Line)
                    .title("Sensor Temperature")
                    .markerRadius(5.0)
                    .markerSymbol(AAChartSymbolType.Circle)
                    .backgroundColor(AAColor.DarkGray)
                    .axesTextColor(AAColor.Black)
                    .legendEnabled(true)
                    .yAxisTitle("Values")
                    .yAxisMax(100)
                    .yAxisMin(60)
                    .stacking(AAChartStackingType.Normal)
                    .dataLabelsEnabled(true)
                    //.xAxisLabelsEnabled(true)
                    .series(
                        arrayOf(
                            AASeriesElement()
                                .name(sensorId)
                                .data(values.toArray())
                                //.data(arrayOf(71, 79, 81, 79, 76))
                                .color(AAColor.Red)
                                .allowPointSelect(true)
                                .dashStyle(AAChartLineDashStyleType.Solid)

                        )
                    )
                    .categories(
                        arrayOf(
                            *dates.toTypedArray()
                        )

                        /*"Mon, 12 Sep 2022 00:00:00 GMT",
                        "Sun, 11 Sep 2022 23:45:00 GMT",
                        "Sun, 11 Sep 2022 23:30:00 GMT",
                        "Sun, 11 Sep 2022 23:15:00 GMT",
                        "Sun, 11 Sep 2022 23:00:00 GMT"*/
                    )

                val aaOptions = aaChartModel.aa_toAAOptions()

                aaOptions.xAxis?.apply {
                    gridLineColor(AAColor.Black)
                        .gridLineWidth(1)
                        .minorGridLineColor(AAColor.Black)
                        .minorGridLineWidth(0.5)
                        .minorTickInterval("auto")
                }

                aaOptions.yAxis?.apply {
                    gridLineColor(AAColor.Black)
                        .gridLineWidth(1)
                        .minorGridLineColor(AAColor.Black)
                        .minorGridLineWidth(0.5)
                        .minorTickInterval("auto")
                }

                aaOptions.legend?.apply {
                    enabled(true)
                        .verticalAlign(AAChartVerticalAlignType.Top)
                        .layout(AAChartLayoutType.Vertical)
                        .align(AAChartAlignType.Right)
                }


                binding.chartView.aa_drawChartWithChartModel(aaChartModel)
                //Grafik Chart Setle
            } ?: run {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
            }
        }

        returnToCamera()

    }

    private fun returnToCamera(){
        binding.backToCamera.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
    }

    companion object {
        var sensorId: String? = null
    }


}