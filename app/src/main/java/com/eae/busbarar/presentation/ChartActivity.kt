package com.eae.busbarar.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.eae.busbarar.R
import com.eae.busbarar.data.model.ChartData
import com.eae.busbarar.data.model.EndDateTime
import com.eae.busbarar.data.model.StartDateTime
import com.eae.busbarar.data.model.TextRecognitionRequest
import com.eae.busbarar.databinding.ActivitySensorBinding
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAChart
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AACrosshair
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AADataLabels
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AALabels
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAPlotOptions
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAScrollablePlotArea
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.github.aachartmodel.aainfographics.aatools.AAColor
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar


@AndroidEntryPoint
class ChartActivity : AppCompatActivity(), ISensor {

    private lateinit var binding : ActivitySensorBinding
    private val viewModel : ChartActivityViewModel by viewModels()
    private val adapter = SensorAdapter(this)
    private val aaChartModel : AAChartModel = AAChartModel()
    private var aaChartView: AAChartView? = null
    private val aaOptions :AAOptions = AAOptions()
    private var chartData : List<ChartData> = listOf()
    private var dates = arrayListOf<String>()
    private var filterStartDateTime: StartDateTime ?= null
    private var filterEndDateTime: EndDateTime ?= null
    private var initialStartMargin: Int = 0
    private var initialTopMargin: Int = 0
    private var isFullscreen = false
    private var toast: Toast? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backToCamera.setOnClickListener {
            startActivity(Intent(this, OpenCameraActivity::class.java))
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
        binding.liveDataChart.setOnClickListener {
            openLiveDataChart()
        }
        binding.chartFullscreen.setOnClickListener {
            if (isFullscreen) {
                revertChartViewMargins()
            } else {
                chartViewFullscreen()
            }
        }
        binding.apply {

            topAppBar.setNavigationOnClickListener {
                menuDrawerLayout.open()
            }

            navigationView.setNavigationItemSelectedListener {
                when(it.itemId) {
                    R.id.firstItem -> {
                        startActivity(Intent(this@ChartActivity, OpenCameraActivity::class.java))
                        Toast.makeText(this@ChartActivity, "Menu Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.secondItem -> {
                        startActivity(Intent(this@ChartActivity, CameraActivity::class.java))
                        Toast.makeText(this@ChartActivity, "Camera Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.thirdItem -> {
                        startActivity(Intent(this@ChartActivity, SensorAddManuelActivity::class.java))
                        Toast.makeText(this@ChartActivity, "Activity Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.forthItem -> {
                        Toast.makeText(this@ChartActivity, "Already Here", Toast.LENGTH_SHORT).show()
                    }
                    R.id.fifthItem -> {
                        startActivity(Intent(this@ChartActivity, AlertScreenActivity::class.java))
                        Toast.makeText(this@ChartActivity, "Alert Screen Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.sixthItem -> {
                        startActivity(Intent(this@ChartActivity, LiveDataActivity::class.java))
                        Toast.makeText(this@ChartActivity, "Live Data Opened", Toast.LENGTH_SHORT).show()
                    }
                }
                it.isChecked = true
                menuDrawerLayout.close()
                true
            }
        }

        setUpRecyclerView()
        lineChartForDataObservation()
        hideSystemNavigationBars()
        drawEmptyCharts()
        //startPeriodicRefresh()

    }

    override fun onBackPressed() {
        startActivity(Intent(this, OpenCameraActivity::class.java))
        finish()
    }

    override fun onStart() {
        super.onStart()
        adapter.list = list
        lastAdded?.let {
            onItemClick(it)
            lastAdded = null
        }
    }

    private fun toast(text: String) {
        toast?.cancel()
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast?.show()
    }

    companion object {
        var list: List<SensorItem> = listOf()
        private var lastAdded: SensorItem ?= null
        var addedSensorIds: HashSet<String> = hashSetOf() // Store the added sensor ids

        fun addSensorItem(item: SensorItem) {
            if (item.sensorId.isNotBlank() && !addedSensorIds.contains(item.sensorId)) {
                list += item
                addedSensorIds.add(item.sensorId)
                lastAdded = item
            }
        }

        fun removeSensorItem(item: SensorItem) {
            list -= item
            addedSensorIds.remove(item.sensorId)
        }
    }

    private fun formatMonthWithLeadingZeros(month: Int): String {
        return String.format("%02d", month)
    }

    private fun getCurrentDateTime(): Calendar {
        return Calendar.getInstance()
    }

    override fun onItemClick(item: SensorItem) {
        item.isSelected = item.isSelected

        if (item.isSelected) {
            val aggvalue = 5
            val currentDateTime = getCurrentDateTime()
            val endFormatted = String.format(
                "%02d:%02d:%02d",
                currentDateTime.get(Calendar.HOUR_OF_DAY),
                currentDateTime.get(Calendar.MINUTE),
                currentDateTime.get(Calendar.SECOND)
            )
            val startDate = currentDateTime.clone() as Calendar
            startDate.add(Calendar.DAY_OF_MONTH, -5)
            val start = "${startDate.get(Calendar.YEAR)}-${formatMonthWithLeadingZeros(startDate.get(Calendar.MONTH) + 1)}-${formatMonthWithLeadingZeros(startDate.get(Calendar.DAY_OF_MONTH))} ${currentDateTime.get(Calendar.HOUR_OF_DAY)}:${currentDateTime.get(Calendar.MINUTE)}:${currentDateTime.get(Calendar.SECOND)}"
            val end = "${currentDateTime.get(Calendar.YEAR)}-${formatMonthWithLeadingZeros(currentDateTime.get(Calendar.MONTH) + 1)}-${formatMonthWithLeadingZeros(currentDateTime.get(Calendar.DAY_OF_MONTH))} $endFormatted"

            viewModel.uploadSensorId(
                TextRecognitionRequest(
                    item.sensorId,
                    aggvalue,
                    start,
                    end
                )
            )
        } else {
            var temp = listOf<ChartData>()
            for (i in chartData) {
                if (i.sensorId != item.sensorId) {
                    temp = temp + listOf(i)
                }
            }
            chartData = temp
            drawChart()
        }
    }

    private fun setUpRecyclerView() {
        adapter.list = list
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(binding.recyclerView) {
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                val deleteButton = deleteButton(position)
                return listOf(deleteButton)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun deleteButton(position: Int) : SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            this,
            "Delete",
            14.0f,
            android.R.color.holo_red_light,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    val item = list[position]
                    removeSensorItem(item)
                    adapter.list = list
                    var temp = listOf<ChartData>()
                    for (i in chartData) {
                        if (item.sensorId != i.sensorId) {
                            temp = temp + listOf(i)
                        }
                    }
                    chartData = temp
                    drawChart()
                    toast("Deleted Sensor ${item.sensorId}")
                }
            })
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

    private fun openLiveDataChart() {
        startActivity(Intent(this, LiveDataActivity::class.java))
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
        adapter.notifyDataSetChanged()
        clearGraph()
    }
    private fun clearGraph() {
        //sensorClicks.clear()
        //chartModels = arrayListOf()
        drawEmptyCharts()
    }

    private fun drawEmptyCharts() {
        val emptyTemp = listOf<AASeriesElement>(AASeriesElement()
            .data(arrayOf())
            .allowPointSelect(true)
            .dashStyle(AAChartLineDashStyleType.Solid))
        aaChartModel
            .chartType(AAChartType.Line)
            .markerRadius(1.0f)
            .markerSymbol(AAChartSymbolType.Circle)
            .backgroundColor(AAColor.DarkGray)
            .axesTextColor(AAColor.Black)
            .touchEventEnabled(false)
            .legendEnabled(false)
            .xAxisReversed(true)
            .stacking(AAChartStackingType.False)
            .dataLabelsEnabled(false)
            .series(
                emptyTemp.toTypedArray()
            )
            .categories(arrayOf())

        binding.chartViewLandscape.aa_drawChartWithChartModel(aaChartModel)
    }

    private fun lineChartForDataObservation() {
        viewModel.sensorResponse.observe(this) { response ->
            response?.let { safeResponse ->
                val values = arrayListOf<Float>()
                val valuesPred = arrayListOf<Int>()
                val valuesClose = arrayListOf<Float>()
                val valuesOpen = arrayListOf<Float>()
                val valuesMax = arrayListOf<Float>()
                val valuesMin = arrayListOf<Float>()
                dates = arrayListOf()
                val sensors = arrayListOf<Int>()
                for (item in safeResponse.listIterator()) {
                    item.datetime?.let {
                        dates.add(it.epochToFormattedString("dd/MM/yy HH:mm"))
                    }
                    item.pred?.let { valuesPred.add(it) }
                    item.average?.let { values.add(it) }
                    item.open?.let { valuesOpen.add(it) }
                    item.close?.let { valuesClose.add(it) }
                    item.min?.let { valuesMin.add(it) }
                    item.max?.let { valuesMax.add(it) }
                    item.sensor?.let { sensors.add(it) }
                }
                chartData = chartData + listOf(
                    ChartData(
                        sensors.component1().toString(),
                        AASeriesElement()
                            .name(sensors.component1().toString())
                            .data(values.toArray())
                            .allowPointSelect(true)
                            .dashStyle(AAChartLineDashStyleType.Solid),
                        AASeriesElement() //Predictions series
                            .name("Prediction " + sensors.component1().toString())
                            .data(valuesPred.toArray())
                            .allowPointSelect(true)
                            .dashStyle(AAChartLineDashStyleType.DashDot),
                        true
                    )
                )
                drawChart()
            } ?: run {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, ChartActivity::class.java))
            }
        }
    }

    private fun drawChart() {
        var temp : List<AASeriesElement> = listOf()
        for (item in chartData) {
            temp = temp + listOf(item.chartElement) // Include average data
            temp = temp + listOf(item.predictionsElement) // Include prediction data
        }
        val aaChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .markerRadius(5.0f)
            .markerSymbol(AAChartSymbolType.TriangleDown)
            .touchEventEnabled(true)
            .zoomType(AAChartZoomType.XY)
            .xAxisReversed(false)
            .scrollablePlotArea(
                AAScrollablePlotArea()
                    .minWidth(650)
                    .scrollPositionX(1f))
            .series(
                temp.toTypedArray()
            )
            .categories(dates.toTypedArray())

        val aaOptions = aaChartModel.aa_toAAOptions()

        aaOptions.series(temp.toTypedArray())

        aaOptions.plotOptions?.line
            ?.dataLabels(AADataLabels()
                .enabled(true)
                .style(AAStyle()
                    .color(AAColor.Black)
                    .fontSize(14)
                    .fontWeight(AAChartFontWeightType.Thin)))

        val aaCrosshair = AACrosshair()
            .dashStyle(AAChartLineDashStyleType.Solid)
            .color(AAColor.White)
            .width(2f)

        val aaLabels = AALabels()
            .useHTML(true)
            .style(AAStyle()
                .fontSize(10)
                .fontWeight(AAChartFontWeightType.Bold)
                .color(AAColor.Black))

        aaOptions.legend?.apply {
            enabled(true)
                .verticalAlign(AAChartVerticalAlignType.Top)
                .layout(AAChartLayoutType.Vertical)
                .align(AAChartAlignType.Right)
        }

        aaOptions.chart?.apply {
            backgroundColor("#C0C0C0")

        }

        aaOptions.yAxis?.apply {
            gridLineWidth(2f)
            gridLineColor("#808080")
            crosshair(aaCrosshair)
                .labels(aaLabels)
        }
        aaOptions.xAxis?.apply {
            gridLineWidth(2f)
            gridLineColor("#808080")
            crosshair(aaCrosshair)
            tickInterval(1)
                .labels(aaLabels)
        }
        binding.chartViewLandscape.aa_drawChartWithChartOptions(aaOptions)
    }
}

fun Int.epochToFormattedString(format: String): String {
    val date = Date(this * 1000L)
    return SimpleDateFormat(format, Locale.getDefault()).format(date)
}
