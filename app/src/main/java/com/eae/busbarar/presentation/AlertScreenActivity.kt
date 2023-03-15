package com.eae.busbarar.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.eae.busbarar.Constants
import com.eae.busbarar.R
import com.eae.busbarar.databinding.ActivityAlertscreenBinding

class AlertScreenActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAlertscreenBinding
    private val URL = Constants.BASE_URL_ALERT_SCREEN
    private val APIKEY = Constants.API_KEY

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
            setBackgroundColor(Color.TRANSPARENT)
            //loadUrl(URL)
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
            val headers = mutableMapOf<String, String>()
            headers["X-EAE-Auth"] = APIKEY
            loadUrl(URL, headers)
        }
    }
}