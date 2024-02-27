package com.kaan.pictograph.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.kaan.pictograph.Constants
import com.kaan.pictograph.R
import com.kaan.pictograph.databinding.ActivityWebviewscreenBinding

class WebViewScreenActivity: AppCompatActivity() {

    private lateinit var binding: ActivityWebviewscreenBinding
    private val URL = Constants.BASE_URL_ALERT_SCREEN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            topAppBar.setNavigationOnClickListener {
                menuDrawerLayout.open()
            }

            navigationView.setNavigationItemSelectedListener {
                when(it.itemId) {
                    R.id.firstItem -> {
                        startActivity(Intent(this@WebViewScreenActivity, OpenCameraActivity::class.java))
                        Toast.makeText(this@WebViewScreenActivity, "Menu Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.secondItem -> {
                        startActivity(Intent(this@WebViewScreenActivity, CameraActivity::class.java))
                        Toast.makeText(this@WebViewScreenActivity, "Camera Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.thirdItem -> {
                        startActivity(Intent(this@WebViewScreenActivity, ItemAddManualActivity::class.java))
                        Toast.makeText(this@WebViewScreenActivity, "Activity Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.forthItem -> {
                        startActivity(Intent(this@WebViewScreenActivity, ItemActivity::class.java))
                        Toast.makeText(this@WebViewScreenActivity, "Chart Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.fifthItem -> {
                        Toast.makeText(this@WebViewScreenActivity, "Already Here", Toast.LENGTH_SHORT).show()
                    }
                    /*R.id.sixthItem -> {
                        startActivity(Intent(this@AlertScreenActivity, LiveDataActivity::class.java))
                        Toast.makeText(this@AlertScreenActivity, "Live Data Opened", Toast.LENGTH_SHORT).show()
                    }*/
                }
                it.isChecked = true
                menuDrawerLayout.close()
                true
            }
        }

        hideSystemNavigationBars()
        alertScreenWebViewIntoAndroidApp()
    }

    private fun hideSystemNavigationBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun alertScreenWebViewIntoAndroidApp() {

        binding.alertScreenWebView.apply {
            WebView.setWebContentsDebuggingEnabled(true)
            webViewClient = object : WebViewClient() {
/*
                // Handle API until level 21
                @Deprecated("Deprecated in Java")
                override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
                    return getNewResponse(url)
                }

                // Handle API 21+
                override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                    val url = request?.url.toString()
                    return getNewResponse(url)
                }

                private fun getNewResponse(url: String?): WebResourceResponse? {
                    return try {
                        val httpClient = OkHttpClient()
                        val request = Request.Builder()
                            .url(url!!)
                            .addHeader("X-EAE-Auth", BuildConfig.API_KEY)
                            .build()
                        val response = httpClient.newCall(request).execute()
                        WebResourceResponse(
                            "text/html",
                            response.header("content-encoding", "utf-8"),
                            response.body!!.byteStream()
                        )
                    } catch (e: Exception) {
                        null
                    }
                }*/
            }
            loadUrl(URL)
            setBackgroundColor(Color.TRANSPARENT)
            settings.apply {
                javaScriptEnabled = true
                cacheMode = WebSettings.LOAD_NO_CACHE
                clearCache(true)
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = true
                textZoom = 100
                setSupportMultipleWindows(true)
                blockNetworkImage = false
                loadsImagesAutomatically = true
                allowContentAccess = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
            }
        }
    }
}
