package com.eae.busbarar.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.eae.busbarar.data.model.TextRecognitionRequest
import com.eae.busbarar.databinding.ActivitySensorBinding
import com.eae.busbarar.databinding.ListItemSensorBinding
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aatools.AAColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SensorActivity : AppCompatActivity(), ISensor{

    private lateinit var binding: ActivitySensorBinding
    private val viewModel: PreviewViewModel by viewModels()
    private val adapter = SensorAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backToCamera.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
        binding.recyclerView.adapter = adapter
        //adapter.list = list

        lineChartForDataObservation()
    }

    override fun onBackPressed() {
        startActivity(Intent(this, CameraActivity::class.java))
        //finish()
    }

    override fun onStart() {
        super.onStart()
        adapter.list = list
    }

    companion object {
        var list: List<String> = listOf()
    }

    override fun onItemClick(item: String) {
        viewModel.uploadSensorId(TextRecognitionRequest(item, 10))
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
                                .name(SensorInfoActivity.sensorId)
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
                binding.chartViewLandscape?.aa_drawChartWithChartModel(aaChartModel)
            } ?: run {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
            }
        }
    }
}