package com.eae.busbarar.presentation

import android.app.AlertDialog
import android.app.AlertDialog.THEME_HOLO_DARK
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.PathDashPathEffect
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.eae.busbarar.R
import com.eae.busbarar.data.model.TextRecognitionRequest
import com.eae.busbarar.databinding.ActivitySensorBinding
import com.eae.busbarar.presentation.SensorInfoActivity.Companion.sensorId
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AADataLabels
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAScrollablePlotArea
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.github.aachartmodel.aainfographics.aatools.AAColor
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class SensorActivity : AppCompatActivity(), ISensor{

    private lateinit var binding : ActivitySensorBinding
    private val viewModel : PreviewViewModel by viewModels()
    private val adapter = SensorAdapter(this)
    private val aaChartModel : AAChartModel = AAChartModel()
    private val chartModels : ArrayList<AASeriesElement> = arrayListOf()
    private val sensorClicks : ArrayList<String> = arrayListOf()
    private val sensorHide : ArrayList<String> = arrayListOf()
    private var dates = arrayListOf<String>()
    private var startDate : String ?= null
    private var endDate : String ?= null

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

        binding.btnShowDateRangePicker?.setOnClickListener {
            showDateRangePicker()
        }
        chartOptions()
        lineChartForDataObservation()
        hideSystemNavigationBars()
        supportActionBar?.hide()
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
                binding.chartViewLandscape?.aa_showTheSeriesElementContent(position)
                sensorHide.remove(item)
            }else{
                binding.chartViewLandscape?.aa_hideTheSeriesElementContent(position)
                sensorHide.add(item)
            }
            /*var position = 0
            sensorClicks.forEachIndexed { index, s ->
                if (item == s)
                    position = index
            }
            chartModels.removeAt(position)
            drawChart()
            sensorClicks.remove(item)*/
            return
        }
        sensorClicks.add(item)

        viewModel.uploadSensorId(TextRecognitionRequest(item, 10, startDate, endDate))
    }

    private fun hideSystemNavigationBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun showDateRangePicker() {
        val dateRangePicker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setTitleText("Select Date")
            .setTheme(R.style.Theme_CameraEAE_NoActionBar)
            .build()

        dateRangePicker.show(
            supportFragmentManager,
            "date_range_picker"
        )

        dateRangePicker.addOnPositiveButtonClickListener { datePicked ->

            startDate = convertLongToDate(datePicked.first)
            endDate = convertLongToDate(datePicked.second)

            binding.tvDateRange?.text = "StartDate: " + startDate + " - EndDate: " + endDate

        }
    }

    private fun convertLongToDate(time:Long): String {

        val date = Date(time)
        val format = SimpleDateFormat(
            "yy-MM-dd",
            Locale.getDefault()
        )
        return format.format(date)
    }

    private fun emptyList() {
        adapter.list = listOf()
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
            .touchEventEnabled(true)
            .legendEnabled(true)
            .yAxisTitle("Values")
            .zoomType(AAChartZoomType.XY)
            .scrollablePlotArea(
                AAScrollablePlotArea()
                    .minWidth(300)
                    .scrollPositionX(1f)
                    .scrollPositionY(1f))
            .stacking(AAChartStackingType.Normal)
            .dataLabelsEnabled(true)
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
                    item.name
                    item.datetime
                    item.pred
                    item.value
                    item.value?.let{values.add(it)}
                    item.datetime?.let{dates.add(it)}
                    item.name?.let{names.add(it)}
                }
                chartModels.add(
                    AASeriesElement()
                        //.dataLabels()
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