package com.eae.busbarar.presentation

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.eae.busbarar.Constants
import com.eae.busbarar.R
import com.eae.busbarar.databinding.ActivityAlertscreenBinding
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class AlertScreenActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAlertscreenBinding
    private val URL = Constants.BASE_URL_ALERT_SCREEN
    private val APIKEY = Constants.API_KEY
    private val USERAGENT = Constants.USER_AGENT
    private val headerMap = HashMap<String, String>()

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

