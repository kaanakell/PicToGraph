package com.eae.busbarar.presentation

import android.content.Intent
import android.os.Bundle
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
import com.eae.busbarar.Constants
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
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class ChartActivity : AppCompatActivity(), ISensor {

    private lateinit var binding : ActivitySensorBinding
    private val viewModel : ChartActivityViewModel by viewModels()
    private val adapter = SensorAdapter(this)
    private val aaChartModel : AAChartModel = AAChartModel()
    private val aaOptions :AAOptions = AAOptions()
    private val chartModels : ArrayList<AASeriesElement> = arrayListOf()
    private val sensorClicks : ArrayList<String> = arrayListOf()
    private val sensorHide : ArrayList<String> = arrayListOf()
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
        private var list: List<SensorItem> = listOf()
        fun addSensorItem(item: SensorItem) {
            var temp : List<SensorItem> = listOf()
            var contains = false
            for (i2 in list) {
                if (i2.sensorId == item.sensorId){
                    contains = true
                }
            }
            if (!contains){
                temp = temp + listOf(item)
            }
            list = list + temp
        }
    }

    override fun onItemClick(item: SensorItem) {
        if(sensorClicks.contains(item.sensorId)) {
            var position = 0
            sensorClicks.forEachIndexed { index, s ->
                if (item.sensorId == s)
                    position = index
            }
            if(sensorHide.contains(item.sensorId)) {
                binding.chartViewLandscape.aa_showTheSeriesElementContent(position + 1)
                sensorHide.remove(item.sensorId)
            }else {
                binding.chartViewLandscape.aa_hideTheSeriesElementContent(position + 1)
                sensorHide.add(item.sensorId)
            }
            return
        }
        sensorClicks.add(item.sensorId)

        val aggvalue = 15
        val start = "${filterStartDateTime?.startTime} ${filterStartDateTime?.startDate}"
        val end = "${filterEndDateTime?.endTime} ${filterEndDateTime?.endDate}"

        viewModel.uploadSensorId(TextRecognitionRequest(item.sensorId, aggvalue, Constants.startDateTime, Constants.endDateTime ))//TODO Api request with aggregation value and start/end date&time
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

    private fun toast(text: String) {
        toast?.cancel()
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast?.show()
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
                    val position =  position
                    val temp = arrayListOf<SensorItem>()
                    list.forEachIndexed { index, s ->
                        if(index != position) {
                            temp.add(s)
                        }
                    }
                    list = temp
                    adapter.list = list
                    binding.chartViewLandscape.aa_removeElementFromChartSeries(position + 1)
                    adapter.notifyDataSetChanged()
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
        adapter.notifyDataSetChanged()
        clearGraph()
    }
    private fun clearGraph() {
        //sensorClicks.clear()
        //chartModels = arrayListOf()
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
                        dates.add(it.epochToFormattedString("dd/MM/yyyy HH:mm:ss"))
                    }
                    item.pred?.let { valuesPred.add(it) }
                    item.average?.let { values.add(it) }
                    item.open?.let { valuesOpen.add(it) }
                    item.close?.let { valuesClose.add(it) }
                    item.min?.let { valuesMin.add(it) }
                    item.max?.let { valuesMax.add(it) }
                    item.sensor?.let { sensors.add(it) }
                }
                chartModels.add(
                    AASeriesElement()
                        .name(sensors.component1().toString())
                        .data(values.toArray())
                        .allowPointSelect(true)
                        .dashStyle(AAChartLineDashStyleType.Solid))
                drawChart()
            } ?: run {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, ChartActivity::class.java))
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

fun Int.epochToFormattedString(format: String): String {
    val date = Date(this * 1000L)
    return SimpleDateFormat(format, Locale.getDefault()).format(date)
}
