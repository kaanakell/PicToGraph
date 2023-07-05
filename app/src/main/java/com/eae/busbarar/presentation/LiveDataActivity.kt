package com.eae.busbarar.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.eae.busbarar.BuildConfig
import com.eae.busbarar.Constants
import com.eae.busbarar.R
import com.eae.busbarar.databinding.ActivityLivedataBinding
import okhttp3.OkHttpClient
import okhttp3.Request


class LiveDataActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLivedataBinding
    private val URL = Constants.BASE_URL_LIVE_DATA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLivedataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            topAppBar.setNavigationOnClickListener {
                menuDrawerLayout.open()
            }

            navigationView.setNavigationItemSelectedListener {
                when(it.itemId) {
                    R.id.firstItem -> {
                        startActivity(Intent(this@LiveDataActivity, OpenCameraActivity::class.java))
                        Toast.makeText(this@LiveDataActivity, "Menu Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.secondItem -> {
                        startActivity(Intent(this@LiveDataActivity, CameraActivity::class.java))
                        Toast.makeText(this@LiveDataActivity, "Camera Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.thirdItem -> {
                        startActivity(Intent(this@LiveDataActivity, SensorAddManuelActivity::class.java))
                        Toast.makeText(this@LiveDataActivity, "Activity Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.forthItem -> {
                        startActivity(Intent(this@LiveDataActivity, ChartActivity::class.java))
                        Toast.makeText(this@LiveDataActivity, "Chart Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.fifthItem -> {
                        startActivity(Intent(this@LiveDataActivity, AlertScreenActivity::class.java))
                        Toast.makeText(this@LiveDataActivity, "Alert Screen Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.sixthItem -> {
                        Toast.makeText(this@LiveDataActivity, "Already Here", Toast.LENGTH_SHORT).show()
                    }
                }
                it.isChecked = true
                menuDrawerLayout.close()
                true
            }
        }

        hideSystemNavigationBars()
        liveDataWebViewIntoAndroidApp()
    }

    private fun hideSystemNavigationBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun liveDataWebViewIntoAndroidApp() {
        binding.liveDataWebView.apply {
            WebView.setWebContentsDebuggingEnabled(true)
            webViewClient = object : WebViewClient() {
               /* // Handle API until level 21
                @Deprecated("Deprecated in Java")
                override fun shouldInterceptRequest(
                    view: WebView?,
                    url: String?
                ): WebResourceResponse? {
                    return getNewResponse(url)
                }

                // Handle API 21+
                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
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

            val selectedSensorIds = ChartActivity.list.filter { it.isSelected }.map { it.sensorId }
            val finalUrl = StringBuilder("$URL?sensor_list=${selectedSensorIds[0]}")
            selectedSensorIds.drop(1).forEach {
                finalUrl.append("&sensor_list=$it")
            }

            Log.e("URL", finalUrl.toString())
            loadUrl(finalUrl.toString())
            setBackgroundColor(Color.TRANSPARENT)
            settings.apply {
                javaScriptEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT
                clearCache(true)
                setSupportZoom(true)
                setSupportMultipleWindows(true)
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