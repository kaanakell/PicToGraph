package com.kaan.pictograph.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.kaan.pictograph.Constants
import com.kaan.pictograph.databinding.ActivityWebviewChartBinding

class WebViewChartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebviewChartBinding
    private val URL = Constants.BASE_URL_WEB_CHART

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //candleStickWebViewIntoAndroidApp()
        hideSystemNavigationBars()
    }

    private fun hideSystemNavigationBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    /*@SuppressLint("SetJavaScriptEnabled")
    private fun candleStickWebViewIntoAndroidApp() {
        val list = ChartActivity.list.toList()
        val finalUrl = StringBuilder("$URL?sensor_list=${list[0]}")
        list.drop(1).forEach {
            finalUrl.append("&sensor_list=$it")
        }

        binding.chartWebView.apply {
            webViewClient = WebViewClient()
            loadUrl(finalUrl.toString())
            setBackgroundColor(Color.TRANSPARENT)
            settings.apply {
                javaScriptEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = true
                textZoom = 100
                blockNetworkImage = false
                loadsImagesAutomatically = true
                allowContentAccess = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
            }
        }
    }*/

}