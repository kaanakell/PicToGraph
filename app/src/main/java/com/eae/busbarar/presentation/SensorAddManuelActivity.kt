package com.eae.busbarar.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.eae.busbarar.databinding.ActivitySensorAddManuelBinding


class SensorAddManuelActivity: AppCompatActivity() {
    private lateinit var binding : ActivitySensorAddManuelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorAddManuelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.submit.setOnClickListener {
            SensorActivity.list = SensorActivity.list + listOf(binding.inputText.text.toString())
            val intent = Intent(this, SensorActivity::class.java)
            startActivity(intent)
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
