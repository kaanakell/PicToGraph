package com.eae.busbarar.presentation

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.eae.busbarar.R
import com.eae.busbarar.databinding.ActivityWebviewChartBinding

class WebViewChartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebviewChartBinding
    //private val URL ="http://dev.eae.lumnion.com/csrender?sensor_list=29"
    private val URL ="http://dev.eae.lumnion.com/csrender?sensor_list=29&sensor_list=28&sensor_list=5&sensor_list=4"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        candleStickWebViewIntoAndroidApp()
        hideSystemNavigationBars()
    }

    private fun hideSystemNavigationBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun candleStickWebViewIntoAndroidApp() {

        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(URL)
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
    }

}