package com.eae.busbarar.presentation

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
import com.eae.busbarar.BuildConfig
import com.eae.busbarar.Constants
import com.eae.busbarar.R
import com.eae.busbarar.databinding.ActivityAlertscreenBinding
import okhttp3.OkHttpClient
import okhttp3.Request

class AlertScreenActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAlertscreenBinding
    private val URL = Constants.BASE_URL_ALERT_SCREEN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlertscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            topAppBar.setNavigationOnClickListener {
                menuDrawerLayout.open()
            }

            navigationView.setNavigationItemSelectedListener {
                when(it.itemId) {
                    R.id.firstItem -> {
                        startActivity(Intent(this@AlertScreenActivity, OpenCameraActivity::class.java))
                        Toast.makeText(this@AlertScreenActivity, "Menu Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.secondItem -> {
                        startActivity(Intent(this@AlertScreenActivity, CameraActivity::class.java))
                        Toast.makeText(this@AlertScreenActivity, "Camera Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.thirdItem -> {
                        startActivity(Intent(this@AlertScreenActivity, SensorAddManuelActivity::class.java))
                        Toast.makeText(this@AlertScreenActivity, "Activity Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.forthItem -> {
                        startActivity(Intent(this@AlertScreenActivity, ChartActivity::class.java))
                        Toast.makeText(this@AlertScreenActivity, "Chart Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.fifthItem -> {
                        Toast.makeText(this@AlertScreenActivity, "Already Here", Toast.LENGTH_SHORT).show()
                    }
                    R.id.sixthItem -> {
                        startActivity(Intent(this@AlertScreenActivity, LiveDataActivity::class.java))
                        Toast.makeText(this@AlertScreenActivity, "Live Data Opened", Toast.LENGTH_SHORT).show()
                    }
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

                // Handle API until level 21
                @Deprecated("Deprecated in Java")
                @Suppress("DEPRECATION")
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
                }
            }
            loadUrl(URL)
            setBackgroundColor(Color.TRANSPARENT)
            settings.apply {
                javaScriptEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT
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
            }
        }
    }
}
