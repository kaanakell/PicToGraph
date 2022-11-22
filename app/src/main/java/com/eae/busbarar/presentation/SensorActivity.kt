package com.eae.busbarar.presentation

import android.app.AlertDialog
import android.app.AlertDialog.THEME_HOLO_DARK
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
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
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class SensorActivity : AppCompatActivity(), ISensor{

    private lateinit var binding : ActivitySensorBinding
    private lateinit var btnDatePickerStart : Button
    private lateinit var tvDatePickerStart : TextView
    private lateinit var tvTimeStart : TextView
    private lateinit var btnTimePickerStart : Button
    private val viewModel : PreviewViewModel by viewModels()
    private val adapter = SensorAdapter(this)
    private val aaChartModel : AAChartModel = AAChartModel()
    private val chartModels : ArrayList<AASeriesElement> = arrayListOf()
    private val sensorClicks : ArrayList<String> = arrayListOf()

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

        btnDatePickerStart = findViewById(R.id.btnDatePickerStart)
        tvDatePickerStart = findViewById(R.id.tvDatePickerStart)


        tvTimeStart = findViewById(R.id.tvTimePickerStart)
        btnTimePickerStart = findViewById(R.id.btnTimePickerStart)

        chartOptions()
        lineChartForDataObservation()
        datePickerStart()
        timePickerStart()
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
        if(sensorClicks.contains(item))
            return
        sensorClicks.add(item)
        viewModel.uploadSensorId(TextRecognitionRequest(item, 10))
    }

    private fun hideSystemNavigationBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun timePickerStart() {
        btnTimePickerStart.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val startHour = currentTime.get(Calendar.HOUR_OF_DAY)
            val startMinute = currentTime.get(Calendar.MINUTE)

            TimePickerDialog(this, THEME_HOLO_DARK, { _, hourOfDay, minute ->
                tvTimeStart.setText("$hourOfDay:$minute")
            }, startHour, startMinute, true).show()
        }
    }


    private fun datePickerStart() {
        val myCalendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLableStart(myCalendar)
        }

        btnDatePickerStart.setOnClickListener {
            DatePickerDialog(this, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

    }


    private fun updateLableStart(myCalendar: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val simpleDateFormat = SimpleDateFormat(myFormat, Locale.US)
        tvDatePickerStart.setText(simpleDateFormat.format(myCalendar.time))
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
                val dates = arrayListOf<String>()
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
                        .name(names.component1())
                        .data(values.toArray())
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

            } ?: run {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
            }
        }
    }
}