package com.eae.busbarar.presentation

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.eae.busbarar.R
import com.eae.busbarar.data.model.EndDateTime
import com.eae.busbarar.data.model.StartDateTime
import com.eae.busbarar.data.model.TextRecognitionRequest
import com.eae.busbarar.databinding.ActivitySensorBinding
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAScrollablePlotArea
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.github.aachartmodel.aainfographics.aatools.AAColor
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SensorActivity : AppCompatActivity(), ISensor {


    private lateinit var binding : ActivitySensorBinding
    private lateinit var  toggle : ActionBarDrawerToggle
    private val viewModel : ChartActivityViewModel by viewModels()
    private val adapter = SensorAdapter(this)
    private val aaChartModel : AAChartModel = AAChartModel()
    private val aaOptions :AAOptions = AAOptions()
    private val chartModels : ArrayList<AASeriesElement> = arrayListOf()
    private val sensorClicks : ArrayList<String> = arrayListOf()
    private val sensorHide : ArrayList<String> = arrayListOf()
    private var dates = arrayListOf<String>()
    private var ndata: Int ?= null
    private var filterStartDateTime: StartDateTime ?= null
    private var filterEndDateTime: EndDateTime ?= null
    private var initialStartMargin: Int = 0
    private var initialTopMargin: Int = 0
    private var isFullscreen = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        binding.backToCamera.setOnClickListener {
            startActivity(Intent(this, OpenCameraActivity::class.java))
        }
        binding.emptyList.setOnClickListener {
            emptyList()
        }
        binding.clearGraph.setOnClickListener {
            clearGraph()
        }
        binding.recyclerView.adapter = adapter

        binding.btnShowStartDateTimeRangePicker.setOnClickListener {
            showDateTimeRangePicker()
        }
        binding.btnClearDateTime.setOnClickListener {
            clearDateTime()
        }
        binding.btnWebView.setOnClickListener {
            openCandleStickChart()
        }
        binding.chartFullscreen.setOnClickListener {
            if (isFullscreen) {
                revertChartViewMargins()
            } else {
                chartViewFullscreen()
            }
        }
        binding.apply {
            toggle = ActionBarDrawerToggle(this@SensorActivity,drawerLayout, myToolbar, R.string.open, R.string.close)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(false)

            navView.setNavigationItemSelectedListener {
                when(it.itemId) {
                    R.id.firstItem ->{
                        //startActivity(Intent(this@SensorActivity, OpenCameraActivity::class.java))
                        Toast.makeText(this@SensorActivity, "First Item Clicked", Toast.LENGTH_SHORT).show()
                    }
                    R.id.secondItem ->{
                        Toast.makeText(this@SensorActivity, "Second Item Clicked", Toast.LENGTH_SHORT).show()
                    }
                    R.id.thirdItem ->{
                        Toast.makeText(this@SensorActivity, "Third Item Clicked", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
        }
        //chartOptions()
        lineChartForDataObservation()
        hideSystemNavigationBars()
        drawEmptyCharts()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, OpenCameraActivity::class.java))
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
        if(sensorClicks.contains(item)) {
            var position = 0
            sensorClicks.forEachIndexed { index, s ->
                if (item == s)
                    position = index
            }
            if(sensorHide.contains(item)) {
                binding.chartViewLandscape.aa_showTheSeriesElementContent(position + 1)
                sensorHide.remove(item)
            }else {
                binding.chartViewLandscape.aa_hideTheSeriesElementContent(position + 1)
                sensorHide.add(item)
            }
            return
        }
        sensorClicks.add(item)

        ndata = 100
        val start = "${filterStartDateTime?.startTime} ${filterStartDateTime?.startDate}"
        val end = "${filterEndDateTime?.endTime} ${filterEndDateTime?.endDate}"

        if(filterStartDateTime == null && filterEndDateTime == null) {
            viewModel.uploadSensorId(TextRecognitionRequest(item, ndata, null, null))
        }else {
            viewModel.uploadSensorId(TextRecognitionRequest(item, ndata, start, end))
        }

    }

    private fun chartViewFullscreen() {
        val layoutParams = binding.chartViewHolder.layoutParams as ConstraintLayout.LayoutParams
        initialStartMargin = layoutParams.leftMargin
        initialTopMargin = layoutParams.topMargin

        layoutParams.leftMargin = 0
        layoutParams.topMargin = 0
        binding.chartViewHolder.layoutParams = layoutParams
        isFullscreen = true
    }

    private fun revertChartViewMargins() {
        val layoutParams = binding.chartViewHolder.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.leftMargin = initialStartMargin
        layoutParams.topMargin = initialTopMargin
        binding.chartViewHolder.layoutParams = layoutParams
        isFullscreen = false
    }


    private fun openCandleStickChart() {
        startActivity(Intent(this, WebViewChartActivity::class.java))
    }

    private fun hideSystemNavigationBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }



    private fun showDateTimeRangePicker() {
        val dialog = StartDateTimePickerDialog(context = this)
        dialog.listener = {
            filterStartDateTime = it
            val text = "${it.startTime}\n ${it.startDate}"
            binding.tvStartDateTimeRange.text = text
            dialog.dismiss()
            val endDialog = EndDateTimePickerDialog(context = this)
            endDialog.listener = {
                filterEndDateTime = it
                val text = "${it.endTime}\n  ${it.endDate}"
                binding.tvEndDateTimeRange.text = text
                endDialog.dismiss()
            }
            endDialog.show()
        }
        dialog.show()
    }

    private fun clearDateTime() {
        binding.tvEndDateTimeRange.text = "End Date Time"
        binding.tvStartDateTimeRange.text = "Start Date Time"
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

    private fun chartOptions(aaOptions: AAOptions) {

        aaOptions.xAxis?.apply {
            gridLineColor(AAColor.Black)
                .gridLineWidth(100f)
                .minorGridLineColor(AAColor.Black)
                .minorGridLineWidth(10f)
                .minorTickInterval("auto")
                .gridLineColor("Red")
        }

        aaOptions.yAxis?.apply {
            gridLineColor(AAColor.Black)
                .gridLineWidth(100f)
                .minorGridLineColor(AAColor.Black)
                .minorGridLineWidth(100f)
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
            .markerRadius(1.0f)
            .markerSymbol(AAChartSymbolType.Circle)
            .backgroundColor(AAColor.DarkGray)
            .axesTextColor(AAColor.Black)
            .touchEventEnabled(false)
            .legendEnabled(false)
            .yAxisTitle("Values")
            .xAxisReversed(true)

            .stacking(AAChartStackingType.False)
            .dataLabelsEnabled(false)
            .series(
                chartModels.toTypedArray()
            )
            .categories(arrayOf())

        binding.chartViewLandscape.aa_drawChartWithChartModel(aaChartModel)
    }

    private fun lineChartForDataObservation() {
        viewModel.sensorResponse.observe(this) { response ->
            response?.let {safeResponse ->
                val values = arrayListOf<Float>()
                dates = arrayListOf()
                val sensors = arrayListOf<String>()
                for (item in safeResponse.temps?: listOf()){
                    item.sensor
                    item.datetime
                    item.pred
                    item.value
                    item.value?.let{values.add(it)}
                    item.datetime?.let{dates.add(it)}
                    item.sensor?.let{sensors.add(it)}
                }
                chartModels.add(
                    AASeriesElement()
                        .name(sensors.component1())
                        .data(values.toArray())
                        .allowPointSelect(true)
                        .dashStyle(AAChartLineDashStyleType.Solid))
                drawChart()
            } ?: run {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, OpenCameraActivity::class.java))
            }
        }
    }


    private fun drawChart() {

        aaChartModel
            .chartType(AAChartType.Line)
            .title("Sensor Temperature")
            .markerRadius(5.0f)
            .markerSymbol(AAChartSymbolType.Circle)
            .backgroundColor(AAColor.DarkGray)
            .axesTextColor(AAColor.Black)
            .touchEventEnabled(true)
            .legendEnabled(false)
            .yAxisTitle("Values")
            .zoomType(AAChartZoomType.XY)
            .xAxisTickInterval(2)
            .xAxisGridLineWidth(10f)
            .scrollablePlotArea(
                AAScrollablePlotArea()
                    .minWidth(3000)
                    .scrollPositionX(1f))
            //.dataLabelsEnabled(true)
            .dataLabelsStyle(AAStyle())
            .series(
                chartModels.toTypedArray()
            )
            .categories(dates.toTypedArray())

        binding.chartViewLandscape.aa_drawChartWithChartModel(aaChartModel)
    }



}