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
import android.content.Intent
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aatools.AAColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SensorInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySensorInfoBinding
    private val viewModel: PreviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorInfoBinding.inflate(layoutInflater)
        binding.sensorNameText.text = sensorId
        setContentView(binding.root)

        sendDataForLineChart()
        returnToCameraActivity()
        lineChartForDataObservation()
    }

    private fun returnToCameraActivity() {
        binding.backToCamera.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
            finish()
        }
    }

    private fun sendDataForLineChart() {
        binding.sendData.setOnClickListener {
            //viewModel.uploadSensorId(TextRecognitionRequest(sensorId, 10))
        }
    }

    private fun lineChartForDataObservation() {
        viewModel.sensorResponse.observe(this) { response ->
            response?.let {safeResponse ->
                val values = arrayListOf<Float>()
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
                    .stacking(AAChartStackingType.Normal)
                    .dataLabelsEnabled(true)
                    .series(
                        arrayOf(
                            AASeriesElement()
                                .name(sensorId)
                                .data(values.toArray())
                                .color(AAColor.Red)
                                .allowPointSelect(true)
                                .dashStyle(AAChartLineDashStyleType.Solid)
                        )
                    )
                    .categories(
                        arrayOf(
                            *dates.toTypedArray()
                        )
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
            } ?: run {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        var sensorId: String? = null
    }


}