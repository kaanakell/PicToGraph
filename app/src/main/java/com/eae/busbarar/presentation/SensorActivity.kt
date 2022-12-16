package com.eae.busbarar.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.eae.busbarar.data.model.*
import com.eae.busbarar.databinding.ActivitySensorBinding
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAScrollablePlotArea
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.github.aachartmodel.aainfographics.aatools.AAColor
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class SensorActivity : AppCompatActivity(), ISensor {


    private lateinit var binding : ActivitySensorBinding
    private val viewModel : PreviewViewModel by viewModels()
    private val adapter = SensorAdapter(this)
    private val aaChartModel : AAChartModel = AAChartModel()
    private val chartModels : ArrayList<AASeriesElement> = arrayListOf()
    private val sensorClicks : ArrayList<String> = arrayListOf()
    private val sensorHide : ArrayList<String> = arrayListOf()
    private var dates = arrayListOf<String>()
    private var ndata: Int ?= null
    private var filterStartDateTime: StartDateTime ?= null
    private var filterEndDateTime: EndDateTime ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backToCamera.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
        binding.emptyList?.setOnClickListener {
            emptyList()
        }
        binding.clearGraph?.setOnClickListener {
            clearGraph()
        }
        binding.recyclerView.adapter = adapter

        binding.btnShowEndDateTimeRangePicker?.setOnClickListener {
            showEndDateTimeRangePicker()
        }
        binding.btnShowStartDateTimeRangePicker?.setOnClickListener {
            showStartDateTimeRangePicker()
        }
        binding.btnClearDateTime?.setOnClickListener {
            clearDateTime()
        }

        chartOptions()
        lineChartForDataObservation()
        hideSystemNavigationBars()
        supportActionBar?.hide()
        drawEmptyCharts()
    }

    override fun onBackPressed() {
        startActivity(Intent(this, CameraActivity::class.java))
        finish()
    }

    override fun onStart() {
        super.onStart()
        adapter.list = list
    }

    companion object {
        var list: List<String> = listOf()
    }

    override fun onItemClick(item: String) {
        if(sensorClicks.contains(item)){
            var position = 0
            sensorClicks.forEachIndexed { index, s ->
                if (item == s)
                    position = index
            }
            if(sensorHide.contains(item)){
                binding.chartViewLandscape?.aa_showTheSeriesElementContent(position + 1)
                sensorHide.remove(item)
            }else{
                binding.chartViewLandscape?.aa_hideTheSeriesElementContent(position + 1)
                sensorHide.add(item)
            }
            return
        }
        sensorClicks.add(item)

        ndata = 10
        val start = "${filterStartDateTime?.startTime} ${filterStartDateTime?.startDate}"
        val end = "${filterEndDateTime?.endTime} ${filterEndDateTime?.endDate}"

        if(filterStartDateTime == null && filterEndDateTime == null){
            viewModel.uploadSensorId(TextRecognitionRequest(item, ndata, null, null))
        }else {
            viewModel.uploadSensorId(TextRecognitionRequest(item, ndata, start, end))
        }

    }

    private fun hideSystemNavigationBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun showStartDateTimeRangePicker() {
        val dialog = StartDateTimePickerDialog(context = this)
        dialog.listener = {
            filterStartDateTime = it
            val text = "${it.startTime} - ${it.startDate}"
            binding.tvStartDateTimeRange?.text = text
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showEndDateTimeRangePicker() {
        val dialog = EndDateTimePickerDialog(context = this)
        dialog.listener = {
            filterEndDateTime = it
            val text = "${it.endTime} - ${it.endDate}"
            binding.tvEndDateTimeRange?.text = text
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun clearDateTime() {
        binding.tvEndDateTimeRange?.text = "End Date Time"
        binding.tvStartDateTimeRange?.text = "Start Date Time"
        filterStartDateTime = null
        filterEndDateTime = null
        adapter.sensorClicks = arrayListOf()
        adapter.notifyDataSetChanged()
        clearGraph()
    }

    private fun emptyList() {
        adapter.list = listOf()
        list = listOf()
    }

    private fun clearGraph() {
        sensorClicks.clear()
        chartModels.clear()
        drawEmptyCharts()
    }

    private fun chartOptions() {
        val aaOptions :AAOptions = aaChartModel.aa_toAAOptions()

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
    }

    private fun drawEmptyCharts() {
        chartModels.add(
            AASeriesElement()
                .data(arrayOf())
                .allowPointSelect(true)
                .dashStyle(AAChartLineDashStyleType.Solid))
        aaChartModel
            .chartType(AAChartType.Line)
            .title("Sensor Temperature")
            .markerRadius(5.0)
            .markerSymbol(AAChartSymbolType.Circle)
            .backgroundColor(AAColor.DarkGray)
            .axesTextColor(AAColor.Black)
            .touchEventEnabled(false)
            .legendEnabled(false)
            .yAxisTitle("Values")
            .stacking(AAChartStackingType.Normal)
            .dataLabelsEnabled(false)
            .series(
                chartModels.toTypedArray()
            )
            .categories(arrayOf())

        binding.chartViewLandscape?.aa_drawChartWithChartModel(aaChartModel)
    }

    private fun lineChartForDataObservation() {
        viewModel.sensorResponse.observe(this) { response ->
            response?.let {safeResponse ->
                val values = arrayListOf<Float>()
                dates = arrayListOf()
                val names = arrayListOf<String>()
                for (item in safeResponse.temps?: listOf()){
                    item.sensor
                    item.datetime
                    item.pred
                    item.value
                    item.value?.let{values.add(it)}
                    item.datetime?.let{dates.add(it)}
                    item.sensor?.let{names.add(it)}
                }
                chartModels.add(
                    AASeriesElement()
                        //.name(names.component1())
                        .data(values.toArray())
                        .allowPointSelect(true)
                        .dashStyle(AAChartLineDashStyleType.Solid))
                drawChart()
            } ?: run {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, CameraActivity::class.java))
            }
        }
    }

    private fun drawChart() {
        aaChartModel
            .chartType(AAChartType.Line)
            .title("Sensor Temperature")
            .markerRadius(5.0)
            .markerSymbol(AAChartSymbolType.Circle)
            .backgroundColor(AAColor.DarkGray)
            .axesTextColor(AAColor.Black)
            .touchEventEnabled(true)
            .legendEnabled(true)
            .yAxisTitle("Values")
            .zoomType(AAChartZoomType.XY)
            .scrollablePlotArea(
                AAScrollablePlotArea()
                    .minWidth(500)
                    .scrollPositionX(1f)
                    .scrollPositionY(1f))
            .stacking(AAChartStackingType.Normal)
            .dataLabelsEnabled(true)
            .dataLabelsStyle(AAStyle())
            .series(
                chartModels.toTypedArray()
            )
            .categories(dates.toTypedArray())

        binding.chartViewLandscape?.aa_drawChartWithChartModel(aaChartModel)
    }
}