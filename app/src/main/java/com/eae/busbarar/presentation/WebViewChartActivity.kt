package com.eae.busbarar.presentation

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.eae.busbarar.R
import com.eae.busbarar.databinding.ActivityWebviewChartBinding

class WebViewChartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebviewChartBinding
    private val URL ="http://dev.eae.lumnion.com/csrender?sensor_list=29"


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = ActivityWebviewChartBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_webview_chart)

        candleStickWebViewIntoAndroidApp()
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
                loadWithOverviewMode
            }
        }
    }

}