package com.eae.busbarar.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.eae.busbarar.R
import com.eae.busbarar.databinding.ActivitySensorAddManuelBinding


class SensorAddManuelActivity: AppCompatActivity() {
    private lateinit var binding : ActivitySensorAddManuelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorAddManuelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.submitButton.setOnClickListener {
            ChartActivity.list = ChartActivity.list + listOf(binding.inputText.text.toString())
            val intent = Intent(this, ChartActivity::class.java)
            startActivity(intent)
        }

        binding.apply {

            topAppBar.setNavigationOnClickListener {
                menuDrawerLayout.open()
            }

            navigationView.setNavigationItemSelectedListener {
                when(it.itemId) {
                    R.id.firstItem -> {
                        startActivity(Intent(this@SensorAddManuelActivity, OpenCameraActivity::class.java))
                        Toast.makeText(this@SensorAddManuelActivity, "Menu Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.secondItem -> {
                        startActivity(Intent(this@SensorAddManuelActivity, CameraActivity::class.java))
                        Toast.makeText(this@SensorAddManuelActivity, "Camera Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.thirdItem -> {
                        Toast.makeText(this@SensorAddManuelActivity, "Already Here", Toast.LENGTH_SHORT).show()
                    }
                    R.id.forthItem -> {
                        startActivity(Intent(this@SensorAddManuelActivity, ChartActivity::class.java))
                        Toast.makeText(this@SensorAddManuelActivity, "Chart Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.fifthItem -> {
                        Toast.makeText(this@SensorAddManuelActivity, "Alert Screen Opened", Toast.LENGTH_SHORT).show()
                    }
                }
                it.isChecked = true
                menuDrawerLayout.close()
                true
            }
        }

        supportActionBar?.hide()
        hideSystemNavigationBars()
    }

    private fun hideSystemNavigationBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

}
