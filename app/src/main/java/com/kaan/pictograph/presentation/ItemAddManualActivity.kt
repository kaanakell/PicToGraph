package com.kaan.pictograph.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.kaan.pictograph.R
import com.kaan.pictograph.databinding.ActivityItemAddManuelBinding


class ItemAddManualActivity: AppCompatActivity() {
    private lateinit var binding : ActivityItemAddManuelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemAddManuelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.submitButton.setOnClickListener {
            ItemActivity.addSensorItem(Item(binding.inputText.text.toString(), true))
            val intent = Intent(this, ItemActivity::class.java)
            startActivity(intent)
        }

        binding.switchCameraButton.setOnClickListener {
            startActivity(Intent(this@ItemAddManualActivity, CameraActivity::class.java))
        }

        binding.apply {

            topAppBar.setNavigationOnClickListener {
                menuDrawerLayout.open()
            }

            navigationView.setNavigationItemSelectedListener {
                when(it.itemId) {
                    R.id.firstItem -> {
                        startActivity(Intent(this@ItemAddManualActivity, OpenCameraActivity::class.java))
                        Toast.makeText(this@ItemAddManualActivity, "Menu Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.secondItem -> {
                        startActivity(Intent(this@ItemAddManualActivity, CameraActivity::class.java))
                        Toast.makeText(this@ItemAddManualActivity, "Camera Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.thirdItem -> {
                        Toast.makeText(this@ItemAddManualActivity, "Already Here", Toast.LENGTH_SHORT).show()
                    }
                    R.id.forthItem -> {
                        startActivity(Intent(this@ItemAddManualActivity, ItemActivity::class.java))
                        Toast.makeText(this@ItemAddManualActivity, "Chart Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.fifthItem -> {
                        startActivity(Intent(this@ItemAddManualActivity, WebViewScreenActivity::class.java))
                        Toast.makeText(this@ItemAddManualActivity, "Alert Screen Opened", Toast.LENGTH_SHORT).show()
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
